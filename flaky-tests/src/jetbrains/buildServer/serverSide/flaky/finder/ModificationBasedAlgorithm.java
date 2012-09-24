/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.finder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.serverSide.STest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class ModificationBasedAlgorithm implements FinderAlgorithm {
  private Map<Long, Integer> myModificationMap;

  public void onStart() {
    myModificationMap = new HashMap<Long, Integer>();
  }

  public void onFinish() {
    myModificationMap = null;
  }

  @Nullable
  public Boolean checkTest(@NotNull STest test, @NotNull List<RawData> rawDataList) {
    myModificationMap.clear();
    for (RawData rawData : rawDataList) {
      long modificationId = rawData.getModificationId();
      int currentStatus = rawData.getStatus();
      Integer status = myModificationMap.get(modificationId);
      if (status != null && status != currentStatus) {
        // The test has different results on same sources.
        return true;
      }
      if (status == null) {
        myModificationMap.put(modificationId, currentStatus);
      }
    }
    return null;
  }
}
