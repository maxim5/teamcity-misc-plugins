/*
 * Copyright (c) 2000-2012 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package jetbrains.buildServer.antixss;

import jetbrains.buildServer.BaseTestCase;
import org.testng.annotations.Test;

/**
 * @author Maxim Podkolzine (maxim.podkolzine@jetbrains.com)
 * @since 8.0
 */
@Test
public class AntiXssUtilTest extends BaseTestCase {
  public void test_find_script_injection() throws Exception {
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<script></script>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<SCRIPT></SCRIPT>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<script>foo</script>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo <script>foo</script>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<script>foo</script> foo"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo <script type='text/javascript'>alert(1)</script> foo"));

    // See TW-17745.
    assertTrue(AntiXssUtil.hasPotentialJsInjection("./../logs\"<script>alert(1)</script>/teamcity-server.log"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("\"><script>alert(1)</script>"));
  }

  public void test_find_onload_injection() throws Exception {
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<img src='' onload=''>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo <bar onload='' /> baz"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo <bar ONLOAD='' /> baz"));
  }

  public void test_find_src_injection() throws Exception {
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<IMG SRC=`javascript:alert(\"RSnake says, 'XSS'\")`>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<IMG SRC=javascript:alert(\"XSS\")>"));
  }

  public void test_no_js_injection() throws Exception {
    assertFalse(AntiXssUtil.hasPotentialJsInjection(""));
    assertFalse(AntiXssUtil.hasPotentialJsInjection("foo"));
    assertFalse(AntiXssUtil.hasPotentialJsInjection("bar"));
    assertFalse(AntiXssUtil.hasPotentialJsInjection("buildTypeId"));
    assertFalse(AntiXssUtil.hasPotentialJsInjection("projectId"));
    assertFalse(AntiXssUtil.hasPotentialJsInjection("tab"));
    assertFalse(AntiXssUtil.hasPotentialJsInjection("bt222"));
    assertFalse(AntiXssUtil.hasPotentialJsInjection("project222"));

    assertFalse(AntiXssUtil.hasPotentialJsInjection("foo bar onload='' baz"));
  }

  // Disabled until a proper pattern
  private void test_find_js_injection() throws Exception {
    assertTrue(AntiXssUtil.hasPotentialJsInjection("')"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("',)"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("',0)"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("\")"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("\",)"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("\",0)"));

    // See TW-17745.
    assertTrue(AntiXssUtil.hasPotentialJsInjection("',);alert(1)//"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("',)});alert(1);//x%0aa({//"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("5f9b9');alert(1);//"));

    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo bar' , 'baz'); alert(1); //"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo bar\" , 'baz'); alert(1); //"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("(\''); alert(1); //"));
  }
}
