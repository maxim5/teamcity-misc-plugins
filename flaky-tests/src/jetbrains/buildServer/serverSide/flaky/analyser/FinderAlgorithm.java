/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.analyser;

import java.util.List;
import jetbrains.buildServer.serverSide.STest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public interface FinderAlgorithm {
  void onStart();

  void onFinish();

  // Definitely YES - true
  // Definitely NO  - false
  // Unknown        - null
  @Nullable
  Boolean checkTest(@NotNull STest test, @NotNull List<RawData> rawDataList);
}
