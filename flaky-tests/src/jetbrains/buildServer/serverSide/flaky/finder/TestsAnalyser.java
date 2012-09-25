/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.finder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SQLRunner;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.db.DBAction;
import jetbrains.buildServer.serverSide.db.DBException;
import jetbrains.buildServer.serverSide.db.DBFunctions;
import jetbrains.buildServer.serverSide.db.SQLRunnerEx;
import jetbrains.buildServer.serverSide.db.queries.GenericQuery;
import jetbrains.buildServer.serverSide.flaky.data.*;
import jetbrains.buildServer.serverSide.stat.TestFailureRate;
import jetbrains.buildServer.serverSide.stat.TestFailuresStatistics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO: performance, possible problems with ReSharper (16`000 flaky tests?), use object pool
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class TestsAnalyser {
  private final TestFailuresStatistics myFailuresStatistics;
  private final SQLRunner mySQLRunner;
  private final List<FinderAlgorithm> myAlgorithms;

  private final TestAnalysisProgress myProgress;
  private final TestAnalysisResultHolder myHolder;

  public TestsAnalyser(@NotNull TestFailuresStatistics failuresStatistics,
                       @NotNull SQLRunner sqlRunner,
                       @NotNull TestAnalysisProgress progress,
                       @NotNull TestAnalysisResultHolder holder) {
    myFailuresStatistics = failuresStatistics;
    mySQLRunner = sqlRunner;
    myProgress = progress;
    myHolder = holder;

    myAlgorithms = new ArrayList<FinderAlgorithm>();
    myAlgorithms.add(new SimpleStatusAlgorithm());
    myAlgorithms.add(new ModificationBasedAlgorithm());
  }

  public void analyseTestsInProject(@NotNull SProject project) {
    if (!myProgress.getLock().compareAndSet(false, true)) {
      return;
    }

    try {
      // Starting...
      myProgress.start("Starting...");
      TestAnalysisResult result = new TestAnalysisResult();
      result.setStartDate(new Date());

      // Preparing data...
      myProgress.setCurrentStep("Preparing data...");
      List<TestData> testDataList = new ArrayList<TestData>();
      algorithmsOnStart();

      try {
        List<RawData> buffer = new ArrayList<RawData>(1024);    // reuse the allocated buffer

        // Collecting failure statistics...
        List<TestFailureRate> allFailingTests = myFailuresStatistics.getFailingTests(project, 0.01f);
        myProgress.setTotalSize(allFailingTests.size());

        // Analysis...
        myProgress.setCurrentStep("Analysing tests failures...");
        for (TestFailureRate testFailureRate : allFailingTests) {
          STest test = testFailureRate.getTest();
          int failureCount = testFailureRate.getFailureCount();
          int successCount = testFailureRate.getSuccessCount();
          int totalCount = failureCount + successCount;

          if (failureCount > 0 && successCount == 0) {
            testDataList.add(new TestData(test));
          } else if (totalCount > 1) {
            TestData testData = getFlakyTestData(test, buffer);
            if (testData != null) {
              testDataList.add(testData);
            }
          }
          myProgress.incDoneSize();
        }
      } finally {
        algorithmsOnFinish();
        result.setFinishDate(new Date());
        result.setTests(testDataList);
        myHolder.putFlakyTestsFor(project, result);
      }
    } finally {
      myProgress.getLock().set(false);
    }
  }

  @Nullable
  private TestData getFlakyTestData(@NotNull STest test, @NotNull final List<RawData> buffer) {
    final long testId = test.getTestNameId();
    final long buildId = 0;
    buffer.clear();

    ((SQLRunnerEx) mySQLRunner).withDB(new DBAction<Object>() {
      public Object run(DBFunctions dbf) throws DBException {
        new GenericQuery<Object>(GET_TEST_DATA_SQL, new GenericQuery.ResultSetProcessor<Object>() {
          @Nullable
          public Object process(ResultSet rs) throws SQLException {
            while (rs.next()) {
              long buildId = rs.getLong(1);
              long testId = rs.getLong(2);
              int status = rs.getInt(3);
              String buildTypeId = rs.getString(4);
              long modificationId = rs.getLong(5);
              String agentName = rs.getString(6);
              long buildStartTime = rs.getLong(7);
              buffer.add(new RawData(buildId, testId, status, buildTypeId,
                                     modificationId, agentName, buildStartTime));
            }
            return null;
          }
        }).execute(dbf.getConnection(), buildId, buildId, buildId, testId);

        return null;
      }
    });

    if (buffer.size() <= 1) {
      // Not enough data to analyze.
      return null;
    }

    // RawDataDetails rawDataDetails = RawDataDetails.compute(buffer);
    return runAlgorithms(test, buffer);
  }

  private void algorithmsOnStart() {
    for (FinderAlgorithm algorithm : myAlgorithms) {
      algorithm.onStart();
    }
  }

  @Nullable
  private TestData runAlgorithms(@NotNull STest test, @NotNull List<RawData> data) {
    for (FinderAlgorithm algorithm : myAlgorithms) {
      Boolean checkResult = algorithm.checkTest(test, data);
      if (checkResult != null) {
        return checkResult ? buildTestData(test, data) : null;
      }
    }
    return null;    // we're not sure about this test.
  }

  private void algorithmsOnFinish() {
    for (FinderAlgorithm algorithm : myAlgorithms) {
      algorithm.onFinish();
    }
  }

  @NotNull
  private TestData buildTestData(@NotNull STest test, @NotNull List<RawData> data) {
    FailureRate failureRate;
    Map<String, FailureRate> buildTypeFailureRates = new HashMap<String, FailureRate>();
    Map<String, FailureRate> agentFailureRates = new HashMap<String, FailureRate>();

    for (RawData rawData : data) {
      String buildTypeId = rawData.getBuildTypeId();
      String agentName = rawData.getAgentName();
      int status = rawData.getStatus();
      boolean failure = status == Status.FAILURE.getPriority();

      failureRate = buildTypeFailureRates.get(buildTypeId);
      if (failureRate == null) {
        failureRate = new FailureRate(1, failure ? 1 : 0);
        buildTypeFailureRates.put(buildTypeId, failureRate);
      } else {
        failureRate.incTotalRuns();
        if (failure) failureRate.incFailures();
      }

      failureRate = agentFailureRates.get(agentName);
      if (failureRate == null) {
        failureRate = new FailureRate(1, failure ? 1 : 0);
        agentFailureRates.put(agentName, failureRate);
      } else {
        failureRate.incTotalRuns();
        if (failure) failureRate.incFailures();
      }
    }

    return new TestData(test, buildTypeFailureRates, agentFailureRates);
  }

  private static final String GET_TEST_DATA_SQL =
    "select ti.build_id, ti.test_name_id, ti.status,                                    \n" +
    "       bs.build_type_id, bs.modification_id,                                       \n" +
    "       h.agent_name, h.build_start_time_server                                     \n" +
    "    from test_info ti                                                              \n" +
    "    join build_state bs on bs.build_id=ti.build_id                                 \n" +
    "                       and bs.build_id is not null and bs.build_id >= ?            \n" +
    "    join history h on h.build_id=ti.build_id                                       \n" +
    "                       and h.build_id is not null and h.build_id >= ?              \n" +
    "    where ti.build_id is not null and ti.build_id >= ?                             \n" +
    "      and ti.test_name_id = ? and ti.status != 0                                   \n";
}