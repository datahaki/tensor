// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testExponents() {
    assertEquals(StaticHelper.exponent(RealScalar.of(0)), 1);
    assertEquals(StaticHelper.exponent(RealScalar.of(0.99)), 2);
    assertEquals(StaticHelper.exponent(RealScalar.of(1)), 2);
    assertEquals(StaticHelper.exponent(RealScalar.of(1.01)), 4);
  }
}
