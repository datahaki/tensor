// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SincTest extends TestCase {
  static final Scalar THRESHOLD = DoubleScalar.of(0.05);

  private static Scalar checkBoth(Scalar scalar) {
    Scalar c = Sinc.of(scalar);
    Scalar s = Sin.of(scalar).divide(scalar);
    assertEquals(Chop._15.of(c.subtract(s)), RealScalar.ZERO);
    return c;
  }

  public void testDouble() {
    double value = .01;
    Scalar c = checkBoth(RealScalar.of(value));
    Scalar s = DoubleScalar.of(Math.sin(value) / value);
    assertEquals(c, s);
  }

  public void testReal() {
    checkBoth(RealScalar.of(0.5));
    checkBoth(RealScalar.of(0.1));
    checkBoth(RealScalar.of(0.05));
    checkBoth(RealScalar.of(0.01));
    checkBoth(RealScalar.of(0.005));
    checkBoth(RealScalar.of(0.001));
    checkBoth(RealScalar.of(0.0005));
    checkBoth(RealScalar.of(0.0001));
  }

  public void testReal2() {
    checkBoth(RealScalar.of(-0.5));
    checkBoth(RealScalar.of(-0.1));
    checkBoth(RealScalar.of(-0.05));
    checkBoth(RealScalar.of(-0.01));
    checkBoth(RealScalar.of(-0.005));
    checkBoth(RealScalar.of(-0.001));
    checkBoth(RealScalar.of(-0.0005));
    checkBoth(RealScalar.of(-0.0001));
  }

  public void testZero() {
    assertEquals(Sinc.of(RealScalar.ZERO), RealScalar.ONE);
    assertEquals(Sinc.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  public void testComplex() {
    checkBoth(Sinc.of(ComplexScalar.of(2, 3.0)));
    checkBoth(Sinc.of(ComplexScalar.of(-0.002, 0.03)));
    checkBoth(Sinc.of(ComplexScalar.of(0.002, -0.003)));
    checkBoth(Sinc.of(ComplexScalar.of(-0.002, -0.003)));
    checkBoth(Sinc.of(ComplexScalar.of(Double.MIN_VALUE, Double.MIN_VALUE)));
    checkBoth(Sinc.of(ComplexScalar.of(Double.MIN_VALUE, -Double.MIN_VALUE)));
    checkBoth(Sinc.of(ComplexScalar.of(-Double.MIN_VALUE, Double.MIN_VALUE)));
    checkBoth(Sinc.of(ComplexScalar.of(-Double.MIN_VALUE, -Double.MIN_VALUE)));
  }

  public void testThreshold() {
    Scalar res1 = Sinc.of(THRESHOLD);
    double val1 = THRESHOLD.number().doubleValue();
    double val0 = val1;
    for (int count = 0; count < 100; ++count)
      val0 = Math.nextDown(val0);
    Scalar res0 = Sinc.of(DoubleScalar.of(val0));
    Tolerance.CHOP.requireClose(res1, res0);
  }

  public void testMin() {
    Scalar eps = DoubleScalar.of(Double.MIN_VALUE);
    Tolerance.CHOP.requireClose(Sinc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  public void testEps() {
    Scalar eps = DoubleScalar.of(1e-12);
    Tolerance.CHOP.requireClose(Sinc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  public void testInfinity() {
    Tolerance.CHOP.requireZero(Sinc.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY));
    Tolerance.CHOP.requireZero(Sinc.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY));
  }

  public void testNan() {
    assertFalse(NumberQ.of(Sinc.FUNCTION.apply(DoubleScalar.INDETERMINATE)));
  }

  public void testQuantity() {
    AssertFail.of(() -> Sinc.FUNCTION.apply(Quantity.of(0, "m")));
  }

  public void testTypeFail() {
    AssertFail.of(() -> Sinc.of(StringScalar.of("some")));
  }
}
