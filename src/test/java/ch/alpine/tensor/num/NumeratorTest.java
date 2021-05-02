// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class NumeratorTest extends TestCase {
  public void testSimple() {
    assertEquals(Numerator.FUNCTION.apply(RationalScalar.of(+2, 3)), RealScalar.of(+2));
    assertEquals(Numerator.FUNCTION.apply(RationalScalar.of(-2, 3)), RealScalar.of(-2));
    assertEquals(Numerator.FUNCTION.apply(Pi.HALF), Pi.HALF);
  }
}
