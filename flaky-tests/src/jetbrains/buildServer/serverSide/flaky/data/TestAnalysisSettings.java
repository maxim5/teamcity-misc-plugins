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
  public static final int DEFAULT_PERIOD_DAYS = 5;
  public static final TestAnalysisSettings DEFAULT_SETTINGS =
    new TestAnalysisSettings(Collections.<String>emptyList(), DEFAULT_PERIOD_DAYS, true, false);
  private static final long DAY = 24 * 60 * 60 * 1000;

  private final Collection<String> myExcludeBuildTypes;
  private final int myAnalyseTimePeriodDays;
  private final boolean myAnalyseFullHistory;
  private final boolean mySpeedUpAlwaysFailing;

  public TestAnalysisSettings(@NotNull Collection<String> excludeBuildTypes,
                              int analyseTimePeriodDays,
                              boolean analyseFullHistory,
                              boolean speedUpAlwaysFailing) {
    myExcludeBuildTypes = excludeBuildTypes;
    myAnalyseTimePeriodDays = analyseTimePeriodDays;
    myAnalyseFullHistory = analyseFullHistory;
    mySpeedUpAlwaysFailing = speedUpAlwaysFailing;
  }

  @NotNull
  public Collection<String> getExcludeBuildTypes() {
    return myExcludeBuildTypes;
  }

  public int getAnalyseTimePeriodDays() {
    return myAnalyseTimePeriodDays;
  }

  public boolean isAnalyseFullHistory() {
    return myAnalyseFullHistory;
  }

  public long getAnalyseTimePeriod() {
    return myAnalyseFullHistory ? -1 : DAY * myAnalyseTimePeriodDays;
  }

  public boolean isSpeedUpAlwaysFailing() {
    return mySpeedUpAlwaysFailing;
  }

  @Override
  public String toString() {
    return "TestAnalysisSettings{" +
           "excludeBuildTypes=" + myExcludeBuildTypes +
           ", analyseTimePeriodDays=" + myAnalyseTimePeriodDays +
           ", analyseFullHistory=" + myAnalyseFullHistory +
           ", speedUpAlwaysFailing=" + mySpeedUpAlwaysFailing +
           '}';
  }
}
