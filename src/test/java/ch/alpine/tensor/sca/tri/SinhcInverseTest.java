// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SinhcInverseTest extends TestCase {
  public void testSimple() {
    assertEquals(SinhcInverse.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  public void testMin() {
    Scalar eps = DoubleScalar.of(Double.MIN_VALUE);
    Tolerance.CHOP.requireClose(SinhcInverse.FUNCTION.apply(eps), RealScalar.ONE);
  }

  public void testEps() {
    Scalar eps = DoubleScalar.of(1e-12);
    Tolerance.CHOP.requireClose(SinhcInverse.FUNCTION.apply(eps), RealScalar.ONE);
  }

  public void testFail() {
    AssertFail.of(() -> SinhcInverse.FUNCTION.apply(Quantity.of(0, "m")));
  }
}
