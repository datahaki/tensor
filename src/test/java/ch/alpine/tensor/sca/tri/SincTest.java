// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class SincTest {
  static final Scalar THRESHOLD = DoubleScalar.of(0.05);

  private static Scalar checkBoth(Scalar scalar) {
    Scalar c = Sinc.of(scalar);
    Scalar s = Sin.of(scalar).divide(scalar);
    assertEquals(Chop._15.of(c.subtract(s)), RealScalar.ZERO);
    return c;
  }

  @Test
  void testDouble() {
    double value = .01;
    Scalar c = checkBoth(RealScalar.of(value));
    Scalar s = DoubleScalar.of(Math.sin(value) / value);
    assertEquals(c, s);
  }

  @Test
  void testReal() {
    checkBoth(RealScalar.of(0.5));
    checkBoth(RealScalar.of(0.1));
    checkBoth(RealScalar.of(0.05));
    checkBoth(RealScalar.of(0.01));
    checkBoth(RealScalar.of(0.005));
    checkBoth(RealScalar.of(0.001));
    checkBoth(RealScalar.of(0.0005));
    checkBoth(RealScalar.of(0.0001));
  }

  @Test
  void testReal2() {
    checkBoth(RealScalar.of(-0.5));
    checkBoth(RealScalar.of(-0.1));
    checkBoth(RealScalar.of(-0.05));
    checkBoth(RealScalar.of(-0.01));
    checkBoth(RealScalar.of(-0.005));
    checkBoth(RealScalar.of(-0.001));
    checkBoth(RealScalar.of(-0.0005));
    checkBoth(RealScalar.of(-0.0001));
  }

  @Test
  void testZero() {
    assertEquals(Sinc.of(RealScalar.ZERO), RealScalar.ONE);
    assertEquals(Sinc.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  @Test
  void testComplex() {
    checkBoth(Sinc.of(ComplexScalar.of(2, 3.0)));
    checkBoth(Sinc.of(ComplexScalar.of(-0.002, 0.03)));
    checkBoth(Sinc.of(ComplexScalar.of(0.002, -0.003)));
    checkBoth(Sinc.of(ComplexScalar.of(-0.002, -0.003)));
    checkBoth(Sinc.of(ComplexScalar.of(Double.MIN_VALUE, Double.MIN_VALUE)));
    checkBoth(Sinc.of(ComplexScalar.of(Double.MIN_VALUE, -Double.MIN_VALUE)));
    checkBoth(Sinc.of(ComplexScalar.of(-Double.MIN_VALUE, Double.MIN_VALUE)));
    checkBoth(Sinc.of(ComplexScalar.of(-Double.MIN_VALUE, -Double.MIN_VALUE)));
  }

  @Test
  void testThreshold() {
    Scalar res1 = Sinc.of(THRESHOLD);
    double val1 = THRESHOLD.number().doubleValue();
    double val0 = val1;
    for (int count = 0; count < 100; ++count)
      val0 = Math.nextDown(val0);
    Scalar res0 = Sinc.of(DoubleScalar.of(val0));
    Tolerance.CHOP.requireClose(res1, res0);
  }

  @Test
  void testMin() {
    Scalar eps = DoubleScalar.of(Double.MIN_VALUE);
    Tolerance.CHOP.requireClose(Sinc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  void testEps() {
    Scalar eps = DoubleScalar.of(1e-12);
    Tolerance.CHOP.requireClose(Sinc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  void testInfinity() {
    Tolerance.CHOP.requireZero(Sinc.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY));
    Tolerance.CHOP.requireZero(Sinc.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY));
  }

  @Test
  void testNan() {
    assertFalse(FiniteScalarQ.of(Sinc.FUNCTION.apply(DoubleScalar.INDETERMINATE)));
  }

  @Test
  void testQuantity() {
    assertThrows(Throw.class, () -> Sinc.FUNCTION.apply(Quantity.of(0, "m")));
  }

  @Test
  void testTypeFail() {
    assertThrows(Throw.class, () -> Sinc.of(StringScalar.of("some")));
  }
}
