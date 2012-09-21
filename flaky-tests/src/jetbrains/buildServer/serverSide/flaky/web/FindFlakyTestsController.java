/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.flaky.data.FlakyTestData;
import jetbrains.buildServer.serverSide.flaky.data.FlakyTests;
import jetbrains.buildServer.serverSide.flaky.FlakyTestsFinder;
import jetbrains.buildServer.serverSide.flaky.FlakyTestsHolder;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class FindFlakyTestsController extends BaseController {
  private final ProjectManager myProjectManager;
  private final FlakyTestsFinder myFinder;
  private final FlakyTestsHolder myHolder;

  public FindFlakyTestsController(@NotNull SBuildServer server,
                                  @NotNull WebControllerManager webControllerManager,
                                  @NotNull ProjectManager projectManager,
                                  @NotNull FlakyTestsFinder finder,
                                  @NotNull FlakyTestsHolder holder) {
    super(server);
    myHolder = holder;
    webControllerManager.registerController("/findFlaky.html", this);
    myProjectManager = projectManager;
    myFinder = finder;
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull HttpServletRequest request,
                                  @NotNull HttpServletResponse response) throws Exception {
    String projectId = request.getParameter("projectId");
    if (projectId == null) {
      return null;
    }

    SProject project = myProjectManager.findProjectById(projectId);
    if (project == null) {
      return null;
    }

    List<FlakyTestData> flakyTestsData = myFinder.getFlakyTests(project);
    FlakyTests flakyTests = new FlakyTests(flakyTestsData);
    myHolder.putFlakyTestsFor(project, flakyTests);

    return null;
  }
}
