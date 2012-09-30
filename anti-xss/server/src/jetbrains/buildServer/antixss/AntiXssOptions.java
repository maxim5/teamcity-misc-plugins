/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.antixss;

import jetbrains.buildServer.serverSide.TeamCityProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class AntiXssOptions {
  public enum Policy {
    ALLOW,
    REPORT_TO_LOG,
    CREATE_SERVER_ERROR,
    DENY
  }

  @NotNull
  public static Policy getPolicyForGetRequests() {
    return fromProperty("teamcity.anti-xss.web.get", Policy.DENY);
  }

  @NotNull
  public static Policy getPolicyForPostRequests() {
    return fromProperty("teamcity.anti-xss.web.post", Policy.REPORT_TO_LOG);
  }

  @NotNull
  public static Policy getPolicyForOtherRequests() {
    return fromProperty("teamcity.anti-xss.web.other", Policy.REPORT_TO_LOG);
  }

  @NotNull
  private static Policy fromProperty(@NotNull String property,
                                     @NotNull Policy defaultPolicy) {
    int value = TeamCityProperties.getInteger(property, -1);
    if (value < 0) {
      return defaultPolicy;
    }

    switch (value) {
      case 0:  return Policy.ALLOW;
      case 1:  return Policy.REPORT_TO_LOG;
      case 2:  return Policy.DENY;
      case 9:  return Policy.CREATE_SERVER_ERROR;
      default: return defaultPolicy;
    }
  }
}
