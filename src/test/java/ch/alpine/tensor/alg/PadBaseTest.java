// code by jph
package ch.alpine.tensor.alg;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class PadBaseTest extends TestCase {
  public void testSimple() {
    assertFalse(Modifier.isPublic(PadBase.class.getModifiers()));
  }
}
