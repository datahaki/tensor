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
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class CoshTest {
  @Test
  void testReal() {
    Scalar c = Cosh.FUNCTION.apply(RealScalar.of(2));
    Scalar s = DoubleScalar.of(Math.cosh(2));
    Scalar t = Cosh.of(RealScalar.of(2));
    assertEquals(c, s);
    assertEquals(c, t);
  }

  @Test
  void testComplex() {
    Scalar c = Cosh.of(ComplexScalar.of(2, 3.));
    // -3.72455 + 0.511823 I
    Scalar s = Scalars.fromString("-3.7245455049153224+0.5118225699873846*I");
    assertEquals(c, s);
  }

  @Test
  void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar scalar = Cosh.of(DecimalScalar.of(new BigDecimal("1.2356", mc), mc.getPrecision()));
    assertInstanceOf(DecimalScalar.class, scalar);
    assertEquals(scalar, DoubleScalar.of(Math.cosh(1.2356)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Cosh.of(Quantity.of(1, "deg")));
  }

  @Test
  void testGaussScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Cosh.of(GaussScalar.of(6, 7)));
  }
}
