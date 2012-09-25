/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.flaky.data.FailureRate;
import jetbrains.buildServer.serverSide.flaky.data.FlakyTestData;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: fail rate
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class FlakyTestWebDetails {
  private final FlakyTestData myFlakyTestData;

  // private final List<SBuildType> myAllBuildTypes;
  private final List<SBuildType> myFailedInBuildTypes;

  // private final List<String> myAllAgents;
  private final List<String> myFailedOnAgents;

  public FlakyTestWebDetails(@NotNull ProjectManager projectManager,
                             @NotNull FlakyTestData testData) {
    myFlakyTestData = testData;

    myFailedInBuildTypes = new ArrayList<SBuildType>();
    for (Map.Entry<String, FailureRate> entry : myFlakyTestData.getBuildTypeFailureRates().entrySet()) {
      if (entry.getValue().hasFailures()) {
        SBuildType buildType = projectManager.findBuildTypeById(entry.getKey());
        if (buildType != null) {
          myFailedInBuildTypes.add(buildType);
        }
      }
    }

    myFailedOnAgents = new ArrayList<String>();
    for (Map.Entry<String, FailureRate> entry : myFlakyTestData.getAgentFailureRates().entrySet()) {
      if (entry.getValue().hasFailures()) {
        myFailedOnAgents.add(entry.getKey());
      }
    }
  }

  @NotNull
  public FlakyTestData getFlakyTestData() {
    return myFlakyTestData;
  }

  public boolean isRunInSingleBuildType() {
    return myFlakyTestData.getBuildTypeFailureRates().size() == 1;
  }

  public boolean isRunOnSingleAgent() {
    return myFlakyTestData.getAgentFailureRates().size() == 1;
  }

  @NotNull
  public List<SBuildType> getFailedInBuildTypes() {
    return myFailedInBuildTypes;
  }

  @NotNull
  public List<String> getFailedOnAgents() {
    return myFailedOnAgents;
  }
}
