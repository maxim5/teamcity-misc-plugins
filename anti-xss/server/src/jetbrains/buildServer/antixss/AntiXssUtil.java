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
  /**
   * Detects a potential JavaScript injection in the string <code>s</code>,
   * given that it may contain any user provided value.
   *
   * @param s a user provided string
   * @return true if a potential JS injection is found in the input string.
   */
  public static boolean hasPotentialJsInjection(@NotNull String s) {
    return findInjectionInHtmlCode(s) || findInjectionInJsCode(s);
  }

  private static final Pattern[] HTML_INJECTION_PATTERNS = new Pattern[] {
    // Script fragments
    // Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
    // src='...'
    // Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // lonely script tags
    Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
    Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // eval(...)
    // Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // expression(...)
    // Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // javascript:...
    Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
    // vbscript:...
    // Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
    // onload(...)=...
    Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
  };

  // Catches the injections that exploit lack of HTML escaping, e.g.
  //
  //   <span>User name: ${name}<span>
  //
  // The usual way to use this vulnerability is to inject "<script>" tag
  // in the ${name}, but there are more tricky ways.
  //
  // See HTML_INJECTION_PATTERNS.
  private static boolean findInjectionInHtmlCode(@NotNull String s) {
    // An assumption: a JS injection is not possible here without HTML tags.
    if (s.indexOf('<') == -1) {
      return false;
    }

    for (Pattern pattern: HTML_INJECTION_PATTERNS) {
      if (pattern.matcher(s).find()) {
        return true;
      }
    }

    return false;
  }

  // Catches the injections that exploit lack of JS escaping, e.g.
  //
  //   <script>foo('${param}');</script>
  //
  // These injections are much harder to identify, because no HTML tags are required,
  // the code is already inside a script block.
  //
  // The usual way is to pass the following char sequence
  //
  //   ');
  //
  // and start arbitrary code after that.
  private static boolean findInjectionInJsCode(@NotNull String s) {
    // An assumption: a JS injection is not possible here without closing parenthesis.
    if (s.indexOf(')') == -1) {
      return false;
    }

    int end = s.indexOf(')');
    return count('\'', s, end) % 2 == 1 ||
           count('\"', s, end) % 2 == 1;
  }

  private static int count(char quoteChar, String s, int end) {
    int counter = 0;
    for (int i = 0; i < end; ++i) {
      char ch = s.charAt(i);
      if (ch == '\\') {
        ++i;  // skip this one and next too
      } else if (ch == quoteChar) {
        ++counter;
      }
    }
    return counter;
  }
}
