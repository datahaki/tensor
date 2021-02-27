// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BartlettWindowTest extends TestCase {
  public void testZero() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testExact() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RationalScalar.of(3, 3465));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RationalScalar.of(1153, 1155));
  }

  public void testExact2() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RationalScalar.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  public void testContinuous() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.of(0.499999999));
    Chop._07.requireZero(scalar);
  }

  public void testSemiExact() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RationalScalar.HALF);
    assertTrue(Scalars.isZero(scalar));
    ExactScalarQ.require(scalar);
  }

  public void testOutside() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
