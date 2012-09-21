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
import jetbrains.buildServer.serverSide.flaky.data.FlakyTests;
import jetbrains.buildServer.serverSide.flaky.FlakyTestsHolder;
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
public class FlakyTestsTab extends ProjectTab {
  private final InvestigationTestRunsHolder myTestRunsHolder;
  private final CurrentProblemsManager myProblemsManager;
  private final STestManager myTestManager;

  private final FlakyTestsHolder myHolder;

  public FlakyTestsTab(@NotNull PagePlaces pagePlaces,
                          @NotNull ProjectManager projectManager,
                          @NotNull PluginDescriptor descriptor,
                          @NotNull InvestigationTestRunsHolder testRunsHolder,
                          @NotNull CurrentProblemsManager problemsManager,
                          @NotNull STestManager testManager,
                          @NotNull FlakyTestsHolder holder) {
    super("flaky", "Flaky tests", pagePlaces, projectManager,
          descriptor.getPluginResourcesPath("/project/flaky.jsp"));
    myTestRunsHolder = testRunsHolder;
    myProblemsManager = problemsManager;
    myTestManager = testManager;
    myHolder = holder;
    setPosition(PositionConstraint.after("mutedProblems"));
  }

  @Override
  protected void fillModel(@NotNull Map<String, Object> model,
                           @NotNull HttpServletRequest request,
                           @NotNull SProject project,
                           @Nullable SUser user) {
    FlakyTests flakyTests = myHolder.getFlakyTestsFor(project);
    FlakyTestsBean bean = new FlakyTestsBean(myTestRunsHolder, myProblemsManager, myTestManager, project, flakyTests);
    model.put("bean", bean);
  }
}
