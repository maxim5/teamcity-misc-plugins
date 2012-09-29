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
  public void test_find_js_injection() throws Exception {
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<script></script>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<script>foo</script>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo <script>foo</script>"));
    assertTrue(AntiXssUtil.hasPotentialJsInjection("<script>foo</script> foo"));

    assertTrue(AntiXssUtil.hasPotentialJsInjection("foo <script type='text/javascript'>alert(1)</script> foo"));

    assertTrue(AntiXssUtil.hasPotentialJsInjection("<img src='' onload=''>"));
  }
}
