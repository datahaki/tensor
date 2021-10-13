// code by jph
package ch.alpine.tensor.itp;

import java.io.IOException;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MitchellNetravaliKernelTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(MitchellNetravaliKernel.of( //
        RationalScalar.of(1, 5), //
        RationalScalar.of(1, 3)));
    Scalar s01 = scalarUnaryOperator.apply(RationalScalar.of(1, 4));
    assertEquals(s01, RationalScalar.of(1561, 1920));
    Scalar s12 = scalarUnaryOperator.apply(RationalScalar.of(5, 4));
    assertEquals(s12, RationalScalar.of(-21, 640));
  }

  public void testDirect() {
    ScalarUnaryOperator mnk = MitchellNetravaliKernel.of(0.2, 1 / 3.0);
    Tolerance.CHOP.requireClose(mnk.apply(RealScalar.of(0.4)), RealScalar.of(0.6581333333333335));
    Tolerance.CHOP.requireClose(mnk.apply(RealScalar.of(-0.4)), RealScalar.of(0.6581333333333335));
    Tolerance.CHOP.requireZero(mnk.apply(RealScalar.of(2.4)));
    AssertFail.of(() -> mnk.apply(Quantity.of(1, "m")));
  }
}
