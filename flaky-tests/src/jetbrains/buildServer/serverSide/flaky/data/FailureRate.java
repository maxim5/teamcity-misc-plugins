/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.data;

import java.io.Serializable;

/**
* @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
* @since 8.0
*/
public class FailureRate implements Serializable {
  private int myTotalRuns;
  private int myFailures;

  public FailureRate(int totalRuns, int failures) {
    myTotalRuns = totalRuns;
    myFailures = failures;
  }

  public int getTotalRuns() {
    return myTotalRuns;
  }

  public int getFailures() {
    return myFailures;
  }

  public boolean isAllFailures() {
    return myTotalRuns == myFailures;
  }

  public void incTotalRuns() {
    ++myTotalRuns;
  }

  public void incFailures() {
    ++myFailures;
  }
}
