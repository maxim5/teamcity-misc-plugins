/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.finder;

import java.util.List;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.STest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class SimpleStatusAlgorithm implements FinderAlgorithm {
  public void onStart() {
    // Do nothing.
  }

  public void onFinish() {
    // Do nothing.
  }

  @Nullable
  public Boolean checkTest(@NotNull STest test, @NotNull List<RawData> rawDataList) {
    int status = rawDataList.get(0).getStatus();
    for (RawData rawData : rawDataList) {
      int currentStatus = rawData.getStatus();
      if (currentStatus != status) {
        return null;
      }
    }

    // Return true if status is always a failure.
    return status != Status.NORMAL.getPriority();
  }
}
