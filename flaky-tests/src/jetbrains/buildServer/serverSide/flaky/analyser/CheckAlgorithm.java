/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.analyser;

import java.util.List;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.flaky.data.TestAnalysisSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public interface CheckAlgorithm {
  void onStart(@NotNull TestAnalysisSettings settings);

  void onFinish();

  // Unknown        - null
  @Nullable
  CheckResult checkTest(@NotNull STest test, @NotNull List<RawData> rawDataList);
}
