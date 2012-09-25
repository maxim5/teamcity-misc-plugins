/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.data;

import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.util.cache.EhCacheUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: why cache expires after the restart
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class TestAnalysisResultHolder {
  private final Cache myCache;

  public TestAnalysisResultHolder(@NotNull EhCacheUtil cacheUtil) {
    myCache = cacheUtil.createCache("tests-analysis");
    myCache.getCacheConfiguration().setEternal(true);
    myCache.getCacheConfiguration().setMaxElementsInMemory(3);
  }

  @NotNull
  public TestAnalysisResult getFlakyTestsFor(@NotNull SProject project) {
    return getFromCache(project);
  }

  public void putFlakyTestsFor(@NotNull SProject project, @NotNull TestAnalysisResult testAnalysisResult) {
    putToCache(project, testAnalysisResult);
  }

  private void putToCache(@NotNull SProject project, @NotNull TestAnalysisResult testAnalysisResult) {
    Element element = new Element(project.getProjectId(), testAnalysisResult);
    myCache.put(element);
  }

  @NotNull
  private TestAnalysisResult getFromCache(@NotNull SProject project) {
    Element element = myCache.getQuiet(project.getProjectId());
    if (element != null) {
      //noinspection unchecked
      return (TestAnalysisResult) element.getValue();
    }
    return TestAnalysisResult.EMPTY_FLAKY_TESTS;
  }
}
