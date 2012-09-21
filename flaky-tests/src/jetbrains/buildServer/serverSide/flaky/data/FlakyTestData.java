/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import jetbrains.buildServer.serverSide.STest;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class FlakyTestData implements Serializable {
  private final long myTestId;
  private final String myProjectId;
  private final boolean myAlwaysFailing;
  private final Map<String, FailureRate> myBuildTypeFailureRates;
  private final Map<String, FailureRate> myAgentFailureRates;

  public FlakyTestData(long testId,
                       @NotNull String projectId,
                       @NotNull Map<String, FailureRate> buildTypeFailureRates,
                       @NotNull Map<String, FailureRate> agentFailureRates) {
    myProjectId = projectId;
    myTestId = testId;
    myBuildTypeFailureRates = buildTypeFailureRates;
    myAgentFailureRates = agentFailureRates;
    myAlwaysFailing = calculareAlwaysFailing(buildTypeFailureRates.values());
  }

  public FlakyTestData(long testId, @NotNull String projectId) {
    this(testId, projectId,
         Collections.<String, FailureRate>emptyMap(),
         Collections.<String, FailureRate>emptyMap());
  }

  public FlakyTestData(@NotNull STest test) {
    this(test.getTestNameId(), test.getProjectId());
  }

  public long getTestId() {
    return myTestId;
  }

  @NotNull
  public String getProjectId() {
    return myProjectId;
  }

  public boolean isAlwaysFailing() {
    return myAlwaysFailing;
  }

  @NotNull
  public Map<String, FailureRate> getBuildTypeFailureRates() {
    return myBuildTypeFailureRates;
  }

  @NotNull
  public Map<String, FailureRate> getAgentFailureRates() {
    return myAgentFailureRates;
  }

  public static class FailureRate implements Serializable {
    private final int myTotalRuns;
    private final int myFailures;

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
  }

  private static boolean calculareAlwaysFailing(@NotNull Collection<FailureRate> failureRates) {
    for (FailureRate failureRate : failureRates) {
      if (!failureRate.isAllFailures()) {
        return false;
      }
    }
    return true;
  }
}
