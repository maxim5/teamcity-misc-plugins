/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.analyser;

import java.util.List;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisSettings;
import jetbrains.buildServer.serverSide.flaky.data.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class StatisticAnalysisAlgorithm implements CheckAlgorithm {
  private final int myMinSeriesNum = 2;
  private final double myAvgSeriesLegth = 2; //1.34;

  public void onStart(@NotNull TestAnalysisSettings settings) {
  }

  public void onFinish() {
  }

  @Nullable
  public CheckResult checkTest(@NotNull STest test, @NotNull List<RawData> rawDataList) {
    int number = 0;
    int totalLength = 0;
    boolean inSeries = false;
    long previousModificationId = -1;

    for (RawData rawData : rawDataList) {
      long modificationId = rawData.getModificationId();
      if (modificationId == previousModificationId) {
        continue;
      }

      int status = rawData.getStatus();
      boolean failure = Status.getStatus(status).isFailed();
      if (inSeries) {
        if (failure) {
          ++totalLength;
        } else {
          inSeries = false;
        }
      } else {
        if (failure) {
          inSeries = true;
          ++number;
          ++totalLength;
        } else {
          inSeries = false;
        }
      }
      previousModificationId = modificationId;
    }


    return (number >= myMinSeriesNum && totalLength < myAvgSeriesLegth * number) ?
             new CheckResult(Type.SUSPICIOUS, new SuspiciousFailureStatisticsReason(number, totalLength)) :
             null;
  }
}
