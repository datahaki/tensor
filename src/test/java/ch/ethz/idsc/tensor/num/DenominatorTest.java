// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class DenominatorTest extends TestCase {
  public void testSimple() {
    assertEquals(Denominator.FUNCTION.apply(RationalScalar.of(+2, 3)), RealScalar.of(3));
    assertEquals(Denominator.FUNCTION.apply(RationalScalar.of(-2, 3)), RealScalar.of(3));
    assertEquals(Denominator.FUNCTION.apply(Pi.HALF), RealScalar.ONE);
  }
}
