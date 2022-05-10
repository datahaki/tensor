// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class SinhTest {
  @Test
  public void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Sinh.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.sinh(2));
    assertEquals(c, Sinh.of(i));
    assertEquals(c, s);
  }

  @Test
  public void testComplex() {
    Scalar c = Sinh.of(ComplexScalar.of(2, 3.));
    // -3.59056 + 0.530921 I
    Scalar s = Scalars.fromString("-3.59056458998578+0.5309210862485197*I");
    assertEquals(c, s);
  }

  @Test
  public void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar scalar = Sinh.of(DecimalScalar.of(new BigDecimal("1.2356", MathContext.DECIMAL128), mc.getPrecision()));
    assertInstanceOf(DecimalScalar.class, scalar);
    Tolerance.CHOP.requireClose(scalar, DoubleScalar.of(Math.sinh(1.2356)));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Sinh.of(Quantity.of(1, "deg")));
  }

  @Test
  public void testGaussScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Sinh.of(GaussScalar.of(6, 7)));
  }
}
