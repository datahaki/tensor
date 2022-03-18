// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class SinhcInverseTest {
  @Test
  public void testSimple() {
    assertEquals(SinhcInverse.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  @Test
  public void testMin() {
    Scalar eps = DoubleScalar.of(Double.MIN_VALUE);
    Tolerance.CHOP.requireClose(SinhcInverse.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  public void testEps() {
    Scalar eps = DoubleScalar.of(1e-12);
    Tolerance.CHOP.requireClose(SinhcInverse.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  public void testFail() {
    AssertFail.of(() -> SinhcInverse.FUNCTION.apply(Quantity.of(0, "m")));
  }
}
