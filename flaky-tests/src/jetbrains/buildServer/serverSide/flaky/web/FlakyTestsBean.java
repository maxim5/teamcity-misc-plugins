/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.controllers.investigate.DummyTestRunImpl;
import jetbrains.buildServer.responsibility.InvestigationTestRunsHolder;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.flaky.data.FlakyTestData;
import jetbrains.buildServer.serverSide.flaky.data.FlakyTests;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.web.problems.GroupedTestsBean;
import org.jetbrains.annotations.NotNull;

/**
 * Use {@link InvestigationTestRunsHolder} and {@link CurrentProblemsManager} to find test runs.
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class FlakyTestsBean {
  private final InvestigationTestRunsHolder myTestRunsHolder;
  private final CurrentProblemsManager myProblemsManager;
  private final STestManager myTestManager;

  private final SProject myProject;
  private final FlakyTests myFlakyTests;

  private final Map<Long, FlakyTestWebDetails> myDetails;

  public FlakyTestsBean(@NotNull InvestigationTestRunsHolder testRunsHolder,
                        @NotNull CurrentProblemsManager problemsManager,
                        @NotNull STestManager testManager,
                        @NotNull ProjectManager projectManager,
                        @NotNull SProject project,
                        @NotNull FlakyTests flakyTests) {
    myTestRunsHolder = testRunsHolder;
    myProblemsManager = problemsManager;
    myTestManager = testManager;
    myProject = project;
    myFlakyTests = flakyTests;
    myDetails = buildDetails(flakyTests, projectManager);
  }

  @NotNull
  public SProject getProject() {
    return myProject;
  }

  public boolean isHasData() {
    return isHasFlaky() || isHasAlwaysFailing();
  }

  public boolean isHasFlaky() {
    return !myFlakyTests.getFlakyTests().isEmpty();
  }

  public boolean isHasAlwaysFailing() {
    return !myFlakyTests.getAlwaysFailingTests().isEmpty();
  }

  public int getFlakyTestsSize() {
    return myFlakyTests.getFlakyTests().size();
  }

  public int getAlwaysFailingTestsSize() {
    return myFlakyTests.getAlwaysFailingTests().size();
  }

  @NotNull
  public GroupedTestsBean getFlakyTests() {
    List<STestRun> testRuns = getTestRuns(myFlakyTests.getFlakyTests());
    return GroupedTestsBean.createForTests(testRuns);
  }

  @NotNull
  public GroupedTestsBean getAlwaysFailingTests() {
    List<STestRun> testRuns = getTestRuns(myFlakyTests.getAlwaysFailingTests());
    return GroupedTestsBean.createForTests(testRuns);
  }

  @NotNull
  public Map<Long, FlakyTestWebDetails> getDetails() {
    return myDetails;
  }

  @NotNull
  private List<STestRun> getTestRuns(@NotNull List<FlakyTestData> data) {
    ArrayList<STestRun> result = new ArrayList<STestRun>();
    for (FlakyTestData flakyTestData : data) {
      long testId = flakyTestData.getTestId();

      List<STestRun> testRuns = myTestRunsHolder.getLastTestRuns(testId, myProject.getProjectId(), false);
      if (testRuns != null && !testRuns.isEmpty()) {
        result.addAll(testRuns);
        continue;
      }

      TestName testName = TestName2Index.getInstance().getTestName(testId);
      if (testName == null) {
        continue;
      }
      STestRun testRun = myProblemsManager.findProblemRunForTest(myProject, testName, true);
      if (testRun == null) {
        TestEx test = (TestEx) myTestManager.findTest(testId, myProject.getProjectId());
        testRun = new DummyTestRunImpl(test);
      }

      result.add(testRun);
    }
    return result;
  }

  @NotNull
  private static Map<Long, FlakyTestWebDetails> buildDetails(@NotNull FlakyTests flakyTests,
                                                             @NotNull ProjectManager projectManager) {
    HashMap<Long, FlakyTestWebDetails> result = new HashMap<Long, FlakyTestWebDetails>();
    for (FlakyTestData data : flakyTests.getFlakyTests()) {
      result.put(data.getTestId(), new FlakyTestWebDetails(projectManager, data));
    }
    for (FlakyTestData data : flakyTests.getAlwaysFailingTests()) {
      result.put(data.getTestId(), new FlakyTestWebDetails(projectManager, data));
    }
    return result;
  }
}
