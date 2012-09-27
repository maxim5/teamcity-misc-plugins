/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class TestAnalysisSettings implements Serializable {
  public static final int DEFAULT_PERIOD = 5 * 24 * 60 * 60 * 1000;
  public static final TestAnalysisSettings DEFAULT_SETTINGS =
    new TestAnalysisSettings(Collections.<String>emptyList(), DEFAULT_PERIOD, true);

  private final Collection<String> myExcludeBuildTypes;
  private final long myAnalyseTimePeriod;
  private final boolean myUseEuristicToFilterAlwaysFailingTests;

  public TestAnalysisSettings(@NotNull Collection<String> excludeBuildTypes,
                              long analyseTimePeriod,
                              boolean useEuristicToFilterAlwaysFailingTests) {
    myExcludeBuildTypes = excludeBuildTypes;
    myAnalyseTimePeriod = analyseTimePeriod;
    myUseEuristicToFilterAlwaysFailingTests = useEuristicToFilterAlwaysFailingTests;
  }

  @NotNull
  public Collection<String> getExcludeBuildTypes() {
    return myExcludeBuildTypes;
  }

  public long getAnalyseTimePeriod() {
    return myAnalyseTimePeriod;
  }

  public boolean isUseEuristicToFilterAlwaysFailingTests() {
    return myUseEuristicToFilterAlwaysFailingTests;
  }
}
