// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;

class MitchellNetravaliKernelTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(MitchellNetravaliKernel.of( //
        Rational.of(1, 5), //
        Rational.of(1, 3)));
    Scalar s01 = scalarUnaryOperator.apply(Rational.of(1, 4));
    assertEquals(s01, Rational.of(1561, 1920));
    Scalar s12 = scalarUnaryOperator.apply(Rational.of(5, 4));
    assertEquals(s12, Rational.of(-21, 640));
  }

  @Test
  void testDirect() {
    ScalarUnaryOperator mnk = MitchellNetravaliKernel.of(0.2, 1 / 3.0);
    Tolerance.CHOP.requireClose(mnk.apply(RealScalar.of(0.4)), RealScalar.of(0.6581333333333335));
    Tolerance.CHOP.requireClose(mnk.apply(RealScalar.of(-0.4)), RealScalar.of(0.6581333333333335));
    Tolerance.CHOP.requireZero(mnk.apply(RealScalar.of(2.4)));
    assertThrows(Throw.class, () -> mnk.apply(Quantity.of(1, "m")));
  }

  @Test
  void testToString() {
    assertEquals(MitchellNetravaliKernel.standard().toString(), "MitchellNetravaliKernel[1/3, 1/3]");
  }
}
