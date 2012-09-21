/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky;

import java.util.ArrayList;
import java.util.List;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.flaky.data.FlakyTestData;
import jetbrains.buildServer.serverSide.stat.TestFailureRate;
import jetbrains.buildServer.serverSide.stat.TestFailuresStatistics;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class FlakyTestsFinder {
  private final TestFailuresStatistics myFailuresStatistics;

  public FlakyTestsFinder(@NotNull TestFailuresStatistics failuresStatistics) {
    myFailuresStatistics = failuresStatistics;
  }

  @NotNull
  public List<FlakyTestData> getFlakyTests(@NotNull SProject project) {
    List<TestFailureRate> allFailingTests = myFailuresStatistics.getFailingTests(project, 0.01f);
    List<Long> possibleFlakyTests = new ArrayList<Long>();

    ArrayList<FlakyTestData> result = new ArrayList<FlakyTestData>();
    for (TestFailureRate testFailureRate : allFailingTests) {
      STest test = testFailureRate.getTest();
      if (testFailureRate.getFailureCount() > 0 && testFailureRate.getSuccessCount() == 0) {
        result.add(new FlakyTestData(test));
      } else {
        possibleFlakyTests.add(test.getTestNameId());
      }
    }

    System.out.println(possibleFlakyTests);
    System.out.println(result);

    return result;
  }
}
