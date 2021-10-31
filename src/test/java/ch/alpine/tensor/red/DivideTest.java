// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RationalScalar;
import junit.framework.TestCase;

public class DivideTest extends TestCase {
  public void testSimple() {
    assertTrue(Divide.nonZero(3, 0).isEmpty());
    assertEquals(Divide.nonZero(3, 5).get(), RationalScalar.of(3, 5));
  }
}
