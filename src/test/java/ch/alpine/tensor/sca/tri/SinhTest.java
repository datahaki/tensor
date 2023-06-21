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
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class SinhTest {
  @Test
  void testReal() {
    Scalar i = RealScalar.of(2);
    Scalar c = Sinh.FUNCTION.apply(i);
    Scalar s = DoubleScalar.of(Math.sinh(2));
    assertEquals(c, Sinh.FUNCTION.apply(i));
    assertEquals(c, s);
  }

  @Test
  void testComplex() {
    Scalar c = Sinh.FUNCTION.apply(ComplexScalar.of(2, 3.));
    Scalar s = ComplexScalar.of(-3.59056458998578, 0.5309210862485197);
    assertEquals(c, s);
  }

  @Test
  void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar scalar = Sinh.FUNCTION.apply(DecimalScalar.of(new BigDecimal("1.2356", MathContext.DECIMAL128), mc.getPrecision()));
    assertInstanceOf(DecimalScalar.class, scalar);
    Tolerance.CHOP.requireClose(scalar, DoubleScalar.of(Math.sinh(1.2356)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> Sinh.FUNCTION.apply(Quantity.of(1, "deg")));
  }

  @Test
  void testGaussScalarFail() {
    assertThrows(Throw.class, () -> Sinh.FUNCTION.apply(GaussScalar.of(6, 7)));
  }
}
