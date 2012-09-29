/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.antixss;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Inspired by this
 * <a href="http://ricardozuasti.com/2012/stronger-anti-cross-site-scripting-xss-filter-for-java-web-apps/">blog post</a>.
 * See also
 * <a href="https://www.owasp.org/index.php/XSS_Filter_Evasion_Cheat_Sheet">XSS Filter Evasion Cheat Sheet</a>.
 *
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
public class AntiXssUtil {
  private static final Pattern[] ourPatterns = new Pattern[]{
    // Script fragments
    // Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
    // src='...'
    // Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // lonely script tags
    Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
    Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // eval(...)
    Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // expression(...)
    // Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // javascript:...
    Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
    // vbscript:...
    // Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
    // onload(...)=...
    Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
  };

  // The pattern below catches these kinds of js injections:
  //
  //   <script>foo('${param}');</script>
  //
  // where ${param} is not properly escaped.
  //
  // But unfortunately the pattern is too strong to be used as is.
  //
  //  Pattern.compile("['\"].*\\).*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
  //

  public static boolean hasPotentialJsInjection(@NotNull String s) {
    // An assumption: a JS injection is possible only via HTML injection.
    if (s.indexOf('<') == -1) {
      return false;
    }

    for (Pattern pattern: ourPatterns) {
      if (pattern.matcher(s).find()) {
        return true;
      }
    }

    return false;
  }
}
