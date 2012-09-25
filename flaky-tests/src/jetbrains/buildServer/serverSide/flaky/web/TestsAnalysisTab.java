/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jetbrains.buildServer.responsibility.InvestigationTestRunsHolder;
import jetbrains.buildServer.serverSide.CurrentProblemsManager;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.STestManager;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisProgress;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisResultHolder;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.project.ProjectTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class TestsAnalysisTab extends ProjectTab {
  private final InvestigationTestRunsHolder myTestRunsHolder;
  private final CurrentProblemsManager myProblemsManager;
  private final STestManager myTestManager;

  private final TestAnalysisProgress myProgress;
  private final TestAnalysisResultHolder myHolder;

  public TestsAnalysisTab(@NotNull PagePlaces pagePlaces,
                          @NotNull ProjectManager projectManager,
                          @NotNull PluginDescriptor descriptor,
                          @NotNull InvestigationTestRunsHolder testRunsHolder,
                          @NotNull CurrentProblemsManager problemsManager,
                          @NotNull STestManager testManager,
                          @NotNull TestAnalysisProgress progress,
                          @NotNull TestAnalysisResultHolder holder) {
    super("analysis", "Tests analysis", pagePlaces, projectManager,
          descriptor.getPluginResourcesPath("/analysis.jsp"));
    myTestRunsHolder = testRunsHolder;
    myProblemsManager = problemsManager;
    myTestManager = testManager;
    myProgress = progress;
    myHolder = holder;

    setPosition(PositionConstraint.after("mutedProblems"));
    addCssFile(descriptor.getPluginResourcesPath("/analysis.css"));
    addCssFile("/css/viewModification.css");
    addJsFile("/js/bs/blocksWithHeader.js");
    addJsFile("/js/bs/buildResultsDiv.js");
    addJsFile("/js/bs/testDetails.js");
  }

  @Override
  protected void fillModel(@NotNull Map<String, Object> model,
                           @NotNull HttpServletRequest request,
                           @NotNull SProject project,
                           @Nullable SUser user) {
    TestsAnalysisBean bean = new TestsAnalysisBean(myTestRunsHolder, myProblemsManager,
                                                   myTestManager, getProjectManager(),
                                                   myProgress, myHolder, project);
    model.put("bean", bean);
  }
}
