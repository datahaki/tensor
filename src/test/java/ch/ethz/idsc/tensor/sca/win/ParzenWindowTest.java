// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ParzenWindowTest extends TestCase {
  public void testSimple() {
    assertEquals(ParzenWindow.FUNCTION.apply(RationalScalar.of(1, 10)), RationalScalar.of(101, 125));
    assertEquals(ParzenWindow.FUNCTION.apply(RationalScalar.of(3, 10)), RationalScalar.of(16, 125));
  }

  public void testSemiExact() {
    Scalar scalar = ParzenWindow.FUNCTION.apply(RationalScalar.HALF);
    assertTrue(Scalars.isZero(scalar));
    ExactScalarQ.require(scalar);
  }

  public void testOutside() {
    Scalar scalar = ParzenWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  public void testQuantityFail() {
    AssertFail.of(() -> ParzenWindow.FUNCTION.apply(Quantity.of(0, "s")));
    AssertFail.of(() -> ParzenWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
