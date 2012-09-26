/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import java.util.*;
import jetbrains.buildServer.controllers.investigate.DummyTestRunImpl;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.responsibility.InvestigationTestRunsHolder;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisProgress;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisResult;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisResultHolder;
import jetbrains.buildServer.serverSide.flaky.data.TestData;
import jetbrains.buildServer.web.problems.GroupedTestsBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO: introduce bulk methods in {@link InvestigationTestRunsHolder} and {@link CurrentProblemsManager} to find test runs.
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class TestsAnalysisBean {
  private final STestManager myTestManager;

  private final SProject myProject;
  private final TestAnalysisResult myTestAnalysisResult;

  private final Map<Long, TestWebDetails> myDetails;

  private final TestAnalysisProgress myProgress;

  public TestsAnalysisBean(@NotNull STestManager testManager,
                           @NotNull ProjectManager projectManager,
                           @NotNull TestAnalysisProgress progress,
                           @NotNull TestAnalysisResultHolder holder,
                           @NotNull SProject project) {
    myTestManager = testManager;
    myProgress = progress;
    myProject = project;

    if (isInProgress()) {
      myTestAnalysisResult = TestAnalysisResult.EMPTY_FLAKY_TESTS;
      myDetails = Collections.emptyMap();
    } else {
      myTestAnalysisResult = holder.getFlakyTestsFor(project);
      myDetails = buildDetails(myTestAnalysisResult, projectManager);
    }
  }

  @NotNull
  public TestAnalysisResult getTestAnalysisResult() {
    return myTestAnalysisResult;
  }

  public boolean isTestAnalysisEverStarted() {
    return myTestAnalysisResult.getStartDate() != null;
  }

  public String getTestAnalysisDuration() {
    Date startDate = myTestAnalysisResult.getStartDate();
    Date finishDate = myTestAnalysisResult.getFinishDate();

    assert startDate != null;
    assert finishDate != null;
    long duration = (finishDate.getTime() - startDate.getTime()) / 1000;
    return duration + " seconds";
  }

  @NotNull
  public SProject getProject() {
    return myProject;
  }

  public boolean isHasData() {
    return isHasFlaky() || isHasAlwaysFailing();
  }

  public boolean isHasFlaky() {
    return !myTestAnalysisResult.getFlakyTests().isEmpty();
  }

  public boolean isHasAlwaysFailing() {
    return !myTestAnalysisResult.getAlwaysFailingTests().isEmpty();
  }

  public int getFlakyTestsSize() {
    return myTestAnalysisResult.getFlakyTests().size();
  }

  public int getAlwaysFailingTestsSize() {
    return myTestAnalysisResult.getAlwaysFailingTests().size();
  }

  @NotNull
  public GroupedTestsBean getFlakyTests() {
    List<STestRun> testRuns = getTestRuns(myTestAnalysisResult.getFlakyTests());
    return GroupedTestsBean.createForTests(testRuns);
  }

  @NotNull
  public GroupedTestsBean getAlwaysFailingTests() {
    List<STestRun> testRuns = getTestRuns(myTestAnalysisResult.getAlwaysFailingTests());
    return GroupedTestsBean.createForTests(testRuns);
  }

  @NotNull
  public Map<Long, TestWebDetails> getDetails() {
    return myDetails;
  }

  @NotNull
  private List<STestRun> getTestRuns(@NotNull List<TestData> data) {
    ArrayList<STestRun> result = new ArrayList<STestRun>();
    for (TestData testData : data) {
      //noinspection deprecation
      TestEx test = (TestEx) myTestManager.createTest(testData.getTestId(), myProject.getProjectId());
      result.add(new DummyTestRunImpl(test) {
        @NotNull
        @Override
        public Status getStatus() {
          return Status.FAILURE;  // A hack to force the link on the web.
        }
      });
    }
    return result;
  }

  @NotNull
  private static Map<Long, TestWebDetails> buildDetails(@NotNull TestAnalysisResult testAnalysisResult,
                                                        @NotNull ProjectManager projectManager) {
    HashMap<Long, TestWebDetails> result = new HashMap<Long, TestWebDetails>();
    for (TestData data : testAnalysisResult.getFlakyTests()) {
      result.put(data.getTestId(), new TestWebDetails(projectManager, data));
    }
    for (TestData data : testAnalysisResult.getAlwaysFailingTests()) {
      result.put(data.getTestId(), new TestWebDetails(projectManager, data));
    }
    return result;
  }

  // Progress

  public boolean isInProgress() {
    return myProgress.getLock().get();
  }

  @Nullable
  public TestAnalysisProgress getProgress() {
    return myProgress;
  }
}
