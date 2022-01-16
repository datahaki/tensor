// code by jph
package ch.alpine.tensor.ext;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class ArgBaseTest extends TestCase {
  public void testVisibility() {
    assertFalse(Modifier.isPublic(ArgBase.class.getModifiers()));
  }
}
