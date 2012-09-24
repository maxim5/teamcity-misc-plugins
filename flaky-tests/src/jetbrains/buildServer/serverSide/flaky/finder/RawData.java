/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.finder;

/**
* @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
* @since 8.0
*/
class RawData {
  private final long myBuildId;
  private final long myTestId;
  private final int myStatus;
  private final String myBuildTypeId;
  private final long myModificationId;
  private final String myAgentName;
  private final long myBuildStartTime;

  RawData(long buildId, long testId, int status, String buildTypeId,
          long modificationId, String agentName, long buildStartTime) {
    myBuildId = buildId;
    myTestId = testId;
    myStatus = status;
    myBuildTypeId = buildTypeId;
    myModificationId = modificationId;
    myAgentName = agentName;
    myBuildStartTime = buildStartTime;
  }

  public long getBuildId() {
    return myBuildId;
  }

  public long getTestId() {
    return myTestId;
  }

  public int getStatus() {
    return myStatus;
  }

  public String getBuildTypeId() {
    return myBuildTypeId;
  }

  public long getModificationId() {
    return myModificationId;
  }

  public String getAgentName() {
    return myAgentName;
  }

  public long getBuildStartTime() {
    return myBuildStartTime;
  }
}
