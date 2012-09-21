/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky;

import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.flaky.data.FlakyTests;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class FlakyTestsHolder {
  private final Cache myCache;

  public FlakyTestsHolder(@NotNull EhCacheUtil cacheUtil) {
    myCache = cacheUtil.createCache("flaky-tests");
    myCache.getCacheConfiguration().setEternal(true);
    myCache.getCacheConfiguration().setMaxElementsInMemory(3);
  }

  @NotNull
  public FlakyTests getFlakyTestsFor(@NotNull SProject project) {
    return getFromCache(project);
  }

  public void putFlakyTestsFor(@NotNull SProject project, @NotNull FlakyTests flakyTests) {
    putToCache(project, flakyTests);
  }

  private void putToCache(@NotNull SProject project, @NotNull FlakyTests flakyTests) {
    Element element = new Element(project.getProjectId(), flakyTests);
    myCache.put(element);
  }

  @NotNull
  private FlakyTests getFromCache(@NotNull SProject project) {
    Element element = myCache.get(project.getProjectId());
    if (element != null) {
      //noinspection unchecked
      return (FlakyTests) element.getValue();
    }
    return FlakyTests.EMPTY_FLAKY_TESTS;
  }
}
