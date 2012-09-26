/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.analyser;

import jetbrains.buildServer.serverSide.flaky.data.Reason;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class BuildsOnSameModificationReason implements Reason {
  private final int myStatus1;
  private final long myBuildId1;
  private final int myStatus2;
  private final long myBuildId2;

  public BuildsOnSameModificationReason(int status1, long buildId1, int status2, long buildId2) {
    myStatus1 = status1;
    myBuildId1 = buildId1;
    myStatus2 = status2;
    myBuildId2 = buildId2;
  }

  public int getStatus1() {
    return myStatus1;
  }

  public long getBuildId1() {
    return myBuildId1;
  }

  public int getStatus2() {
    return myStatus2;
  }

  public long getBuildId2() {
    return myBuildId2;
  }
}
