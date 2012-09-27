/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.flaky.analyser.TestsAnalyser;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisSettings;
import jetbrains.buildServer.util.StringUtil;
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

    final TestAnalysisSettings settings = getSettingsFromRequest(request);

    new Thread(new Runnable() {
      public void run() {
        myAnalyser.analyseTestsInProject(project, settings);
      }
    }, "Tests analyser").start();

    return null;
  }

  @NotNull
  private static TestAnalysisSettings getSettingsFromRequest(@NotNull HttpServletRequest request) {
    String excludeBuildTypesParam = request.getParameter("excludeBuildTypes");
    List<String> excludeBuildTypes = (excludeBuildTypesParam != null) ?
                                       StringUtil.split(excludeBuildTypesParam, true, ':') :
                                       Collections.<String>emptyList();

    String periodParam = request.getParameter("period");
    long analyseTimePeriod = -1;
    if (periodParam != null) {
      try {
        analyseTimePeriod = Long.parseLong(periodParam);
      } catch (NumberFormatException e) {
        // ignore
      }
    }

    boolean analyseAllTests = "true".equals(request.getParameter("analyseAllTests"));
    boolean speedUpAlwaysFailing = "true".equals(request.getParameter("speedUpAlwaysFailing"));

    return new TestAnalysisSettings(excludeBuildTypes, analyseTimePeriod, analyseAllTests, speedUpAlwaysFailing);
  }
}
