/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.antixss;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.CriticalErrors;
import org.jetbrains.annotations.NotNull;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class AntiXssProcessor {
  private static final Logger LOG = Loggers.SERVER;
  private static final String ERROR_KEY = "xss-warning";

  private final CriticalErrors myCriticalErrors;

  public AntiXssProcessor(@NotNull CriticalErrors criticalErrors) {
    myCriticalErrors = criticalErrors;
  }

  public void process(@NotNull AntiXssOptions.Policy policy,
                      @NotNull String shortDescription,
                      @NotNull String longDescription) {
    switch (policy) {
      case ALLOW: {
        return;
      }
      case REPORT_TO_LOG: {
        LOG.warn(longDescription);
        break;
      }
      case CREATE_SERVER_ERROR: {
        // TODO: avoid double warning in the log.
        // TODO: provide a way to clear the error.
        myCriticalErrors.putError(ERROR_KEY, shortDescription + ". Please see details in the log");
        LOG.warn(longDescription);
        break;
      }
      case DENY: {
        LOG.warn(longDescription);
        throw new XssDetectedException(shortDescription + ". Please contact your system administrator");
      }
    }
  }
}
