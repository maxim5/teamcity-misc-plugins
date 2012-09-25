/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.flaky.data.FailureRate;
import jetbrains.buildServer.serverSide.flaky.data.TestData;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: calculate fail rate
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class TestWebDetails {
  private final TestData myTestData;

  private final List<SBuildType> myAllBuildTypes;
  private final List<SBuildType> myFailedInBuildTypes;

  // private final List<String> myAllAgents;
  private final List<String> myFailedOnAgents;

  public TestWebDetails(@NotNull ProjectManager projectManager,
                        @NotNull TestData testData) {
    myTestData = testData;

    myAllBuildTypes = new ArrayList<SBuildType>();
    myFailedInBuildTypes = new ArrayList<SBuildType>();
    for (Map.Entry<String, FailureRate> entry : myTestData.getBuildTypeFailureRates().entrySet()) {
      SBuildType buildType = projectManager.findBuildTypeById(entry.getKey());
      if (buildType != null) {
        if (entry.getValue().hasFailures()) {
          myFailedInBuildTypes.add(buildType);
        }
        myAllBuildTypes.add(buildType);
      }
    }
    Collections.sort(myAllBuildTypes);
    Collections.sort(myFailedInBuildTypes);

    myFailedOnAgents = new ArrayList<String>();
    for (Map.Entry<String, FailureRate> entry : myTestData.getAgentFailureRates().entrySet()) {
      if (entry.getValue().hasFailures()) {
        myFailedOnAgents.add(entry.getKey());
      }
    }
    Collections.sort(myFailedOnAgents);
  }

  @NotNull
  public TestData getTestData() {
    return myTestData;
  }

  public boolean isRunInSingleBuildType() {
    return myTestData.getBuildTypeFailureRates().size() == 1;
  }

  public boolean isRunOnSingleAgent() {
    return myTestData.getAgentFailureRates().size() == 1;
  }

  @NotNull
  public List<SBuildType> getFailedInBuildTypes() {
    return myFailedInBuildTypes;
  }

  @NotNull
  public List<String> getFailedOnAgents() {
    return myFailedOnAgents;
  }

  @NotNull
  public List<SBuildType> getAllBuildTypes() {
    return myAllBuildTypes;
  }
}
