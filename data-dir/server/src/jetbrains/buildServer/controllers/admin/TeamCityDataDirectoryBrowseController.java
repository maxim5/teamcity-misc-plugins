/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.controllers.admin;

import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.controllers.FileBrowseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.SecurityContext;
import jetbrains.buildServer.util.browser.Element;
import jetbrains.buildServer.web.WebAccessHelper;
import jetbrains.buildServer.web.WebAccessService;
import jetbrains.buildServer.web.openapi.*;
import jetbrains.buildServer.web.util.lazytree.DefaultLazyTreeElementRenderer;
import jetbrains.buildServer.web.util.lazytree.LazyTreeElementRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Map;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class TeamCityDataDirectoryBrowseController extends FileBrowseController {
  public TeamCityDataDirectoryBrowseController(@NotNull SBuildServer server,
                                               @NotNull SecurityContext securityContext,
                                               @NotNull ServerPaths serverPaths,
                                               @NotNull WebControllerManager webControllerManager,
                                               @NotNull AuthorizationInterceptor interceptor,
                                               @NotNull WebAccessService webAccessService,
                                               @NotNull PagePlaces pagePlaces,
                                               @NotNull PluginDescriptor pluginDescriptor) {
    super(server, securityContext, webControllerManager, interceptor, webAccessService,
          serverPaths.getDataDirectory(), "/admin/dataDir.html");
    new DataDirectoryBrowseExtension(pagePlaces, pluginDescriptor).register();
  }

  @NotNull
  @Override
  protected Permission getPermission() {
    return Permission.CHANGE_SERVER_SETTINGS;
  }

  @NotNull
  @Override
  protected String getTreeName() {
    return "data-dir-browse";
  }

  @NotNull
  @Override
  protected String getRootDirectoryDescription() {
    return "TeamCity Data Directory";
  }

  @NotNull
  @Override
  protected LazyTreeElementRenderer getTreeElementRenderer() {
    return new DataDirectoryTreeRenderer();
  }

  @NotNull
  @Override
  protected WebAccessHelper getWebAccessHelper() {
    return new WebAccessHelper() {
      @Nullable
      public String getDesiredId() {
        return "dataDir";
      }
      public boolean allowDownloadZip() {
        return false;
      }
      @Nullable
      public String getFileNameForZip() {
        return null;
      }
    };
  }

  @Override
  protected boolean isUploadSupported() {
    return false;
  }

  @Override
  protected boolean isEditSupported() {
    return true;
  }

  @NotNull
  @Override
  protected String getUploadResponseJsBase() {
    return "";
  }

  private class DataDirectoryBrowseExtension extends SimpleCustomTab {
    public DataDirectoryBrowseExtension(@NotNull PagePlaces pagePlaces,
                                        @NotNull PluginDescriptor pluginDescriptor) {
      super(pagePlaces, PlaceId.ADMIN_SERVER_DIAGNOSTIC_TAB, "dataDir",
            pluginDescriptor.getPluginResourcesPath("/admin/dataDir.jsp"), "Browse Data Directory");
      addCssFile(pluginDescriptor.getPluginResourcesPath("/admin/dataDir.css"));
      addCssFile("/css/highlight-idea.css");
      addJsFile("/js/highlight.pack.js");
      setPosition(PositionConstraint.last());
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
      DataDirectoryBean bean = new DataDirectoryBean(getFileFromRequest(request));
      model.put("bean", bean);
    }
  }

  public class DataDirectoryBean extends FileBrowseBean {
    public DataDirectoryBean(@Nullable File file) {
      super(file);
    }

    @Override
    public boolean isShowEmptyFiles() {
      return false;
    }

    @Override
    public String getFileName() {
      return getRelativePath(myFile.getAbsolutePath());
    }
  }

  private class DataDirectoryTreeRenderer extends DefaultLazyTreeElementRenderer {
    private String myPath;

    @Override
    public boolean ignoreElement(@NotNull Element element) {
      return super.ignoreElement(element) || (element.isLeaf() && element.getSize() == 0);
    }

    @Override
    public void prepareForRequest(@NotNull HttpServletRequest request) {
      myPath = request.getContextPath() + "/admin/admin.html?item=diagnostics&tab=dataDir";
    }

    @Override
    public String getHrefForLeaf(@NotNull Element leaf) {
      return isInsideZip(leaf) ? null : myPath + "&file=" + getRelativePath(leaf.getFullName());
    }
  }
}
