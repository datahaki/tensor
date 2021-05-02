// code by jph
package ch.alpine.tensor.sca;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SinhTest extends TestCase {
  public void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Sinh.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.sinh(2));
    assertEquals(c, Sinh.of(i));
    assertEquals(c, s);
  }

  public void testComplex() {
    Scalar c = Sinh.of(ComplexScalar.of(2, 3.));
    // -3.59056 + 0.530921 I
    Scalar s = Scalars.fromString("-3.59056458998578+0.5309210862485197*I");
    assertEquals(c, s);
  }

  public void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar scalar = Sinh.of(DecimalScalar.of(new BigDecimal("1.2356", MathContext.DECIMAL128), mc.getPrecision()));
    assertTrue(scalar instanceof DecimalScalar);
    Tolerance.CHOP.requireClose(scalar, DoubleScalar.of(Math.sinh(1.2356)));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> Sinh.of(Quantity.of(1, "deg")));
  }

  public void testGaussScalarFail() {
    AssertFail.of(() -> Sinh.of(GaussScalar.of(6, 7)));
  }
}
