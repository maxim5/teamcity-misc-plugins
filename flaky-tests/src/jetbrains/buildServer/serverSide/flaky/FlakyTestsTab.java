/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
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
  protected FlakyTestsTab(@NotNull PagePlaces pagePlaces,
                          @NotNull ProjectManager projectManager,
                          @NotNull PluginDescriptor descriptor) {
    super("flaky", "Flaky tests", pagePlaces, projectManager,
          descriptor.getPluginResourcesPath("/project/flaky.jsp"));
    setPosition(PositionConstraint.after("mutedProblems"));
  }

  @Override
  protected void fillModel(@NotNull Map<String, Object> model,
                           @NotNull HttpServletRequest request,
                           @NotNull SProject project,
                           @Nullable SUser user) {
    System.out.println("fill");
  }
}
