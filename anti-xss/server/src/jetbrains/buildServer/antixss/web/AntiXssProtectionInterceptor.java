/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.antixss.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.antixss.AntiXssOptions;
import jetbrains.buildServer.antixss.AntiXssProcessor;
import jetbrains.buildServer.antixss.AntiXssUtil;
import jetbrains.buildServer.antixss.XssDetectedException;
import jetbrains.buildServer.controllers.interceptors.RequestInterceptors;
import jetbrains.buildServer.serverSide.impl.LogUtil;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * See TW-21070.
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class AntiXssProtectionInterceptor extends HandlerInterceptorAdapter {
  private static final String WARNING = "Probable security violation attempt via HTTP request";
  private final AntiXssProcessor myProcessor;

  public AntiXssProtectionInterceptor(@NotNull RequestInterceptors requestInterceptors,
                                      @NotNull AntiXssProcessor processor) {
    myProcessor = processor;
    //requestInterceptors.addInterceptor(this);
  }

  @Override
  public boolean preHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           Object handler) throws Exception {
    try {
      checkForJsInjection(request);
    } catch (XssDetectedException e) {
      handleDeny(response, e.getMessage());
      return false;
    }

    return true;
  }

  private void checkForJsInjection(@NotNull HttpServletRequest request) {
    AntiXssOptions.Policy policy;
    String method = request.getMethod();
    if ("GET".equalsIgnoreCase(method)) {
      policy = AntiXssOptions.getPolicyForGetRequests();
    } else if ("POST".equalsIgnoreCase(method)) {
      policy = AntiXssOptions.getPolicyForPostRequests();
    } else if ("PUT".equalsIgnoreCase(method)) {
      policy = AntiXssOptions.getPolicyForPutRequests();
    } else {
      policy = AntiXssOptions.getPolicyForOtherRequests();
    }

    if (policy == AntiXssOptions.Policy.ALLOW) {
      return;
    }

    boolean injectionFound = false;
    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
      String[] values = entry.getValue();
      if (values.length == 1) {
        if (AntiXssUtil.hasPotentialJsInjection(values[0])) {
          injectionFound = true;
          break;
        }
      } else {
        for (String value : values) {
          if (AntiXssUtil.hasPotentialJsInjection(value)) {
            injectionFound = true;
            break;
          }
        }
      }
    }

    if (injectionFound) {
      myProcessor.process(policy, WARNING, requestToString(request));
    }
  }

  @NotNull
  private static String requestToString(@NotNull HttpServletRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(WARNING).append(": ").append(request.getMethod())
           .append(" ").append(request.getRequestURI()).append(" parameters:\n");
    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
      builder.append("  ").append(entry.getKey()).append(" = ")
             .append(Arrays.toString(entry.getValue())).append("\n");
    }

    SUser user = SessionUser.getUser(request);
    if (user != null) {
      builder.append("Authenticated user: ").append(LogUtil.describe(user));
    }

    return builder.toString();
  }

  private static void handleDeny(@NotNull HttpServletResponse response,
                                 @NotNull String message) throws IOException {
    response.setStatus(403);
    ServletOutputStream output = response.getOutputStream();
    try {
      output.print("<!DOCTYPE html><html>" +
                     "<head><title>Forbidden</title></head>" +
                     "<body><h1>HTTP 403 Forbidden. " + message + "</h1></body>" +
                   "</html>");
    } finally {
      output.close();
    }
  }
}
