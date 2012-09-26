/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.flaky.analyser.TestsAnalyser;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class RunTestsAnalysisController extends BaseController {
  private final ProjectManager myProjectManager;
  private final TestsAnalyser myAnalyser;

  public RunTestsAnalysisController(@NotNull SBuildServer server,
                                    @NotNull WebControllerManager webControllerManager,
                                    @NotNull ProjectManager projectManager,
                                    @NotNull TestsAnalyser analyser) {
    super(server);
    webControllerManager.registerController("/analyzeTests.html", this);
    myProjectManager = projectManager;
    myAnalyser = analyser;
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull HttpServletRequest request,
                                  @NotNull HttpServletResponse response) throws Exception {
    String projectId = request.getParameter("projectId");
    if (projectId == null) {
      return null;
    }

    final SProject project = myProjectManager.findProjectById(projectId);
    if (project == null) {
      return null;
    }

    new Thread(new Runnable() {
      public void run() {
        myAnalyser.analyseTestsInProject(project);
      }
    }, "Tests analyser").start();

    return null;
  }
}
