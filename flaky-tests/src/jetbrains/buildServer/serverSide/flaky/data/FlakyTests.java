/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class FlakyTests implements Serializable {
  public static final FlakyTests EMPTY_FLAKY_TESTS = new FlakyTests(Collections.<FlakyTestData>emptyList(),
                                                                    Collections.<FlakyTestData>emptyList());

  private final List<FlakyTestData> myFlakyTests;
  private final List<FlakyTestData> myAlwaysFailingTests;

  public FlakyTests(@NotNull List<FlakyTestData> allTests) {
    myFlakyTests = new ArrayList<FlakyTestData>();
    myAlwaysFailingTests = new ArrayList<FlakyTestData>();
    for (FlakyTestData test : allTests) {
      if (test.isAlwaysFailing()) {
        myAlwaysFailingTests.add(test);
      } else {
        myFlakyTests.add(test);
      }
    }
  }

  public FlakyTests(@NotNull List<FlakyTestData> flakyTests,
                    @NotNull List<FlakyTestData> alwaysFailingTests) {
    myFlakyTests = flakyTests;
    myAlwaysFailingTests = alwaysFailingTests;
  }

  @NotNull
  public List<FlakyTestData> getFlakyTests() {
    return myFlakyTests;
  }

  @NotNull
  public List<FlakyTestData> getAlwaysFailingTests() {
    return myAlwaysFailingTests;
  }
}
