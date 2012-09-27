/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.serverSide.flaky.data;

/**
* @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
* @since 8.0
*/
public enum Type {
  ORDINARY,
  FLAKY,
  ALWAYS_FAILING;

  public boolean isOrdinary() {
    return this == ORDINARY;
  }

  public boolean isFlaky() {
    return this == FLAKY;
  }

  public boolean isAlwaysFailing() {
    return this == ALWAYS_FAILING;
  }
}
