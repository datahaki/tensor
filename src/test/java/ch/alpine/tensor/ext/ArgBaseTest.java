// code by jph
package ch.alpine.tensor.ext;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class ArgBaseTest extends TestCase {
  public void testVisibility() {
    assertEquals(ArgBase.class.getModifiers() & Modifier.PUBLIC, 0);
  }
}
