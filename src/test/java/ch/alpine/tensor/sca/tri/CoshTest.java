// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class CoshTest {
  @Test
  void testReal() {
    Scalar c = Cosh.FUNCTION.apply(RealScalar.of(2));
    Scalar s = DoubleScalar.of(Math.cosh(2));
    Scalar t = Cosh.FUNCTION.apply(RealScalar.of(2));
    assertEquals(c, s);
    assertEquals(c, t);
  }

  @Test
  void testComplex() {
    Scalar c = Cosh.FUNCTION.apply(ComplexScalar.of(2, 3.));
    Scalar s = ComplexScalar.of(-3.7245455049153224, +0.5118225699873846);
    assertEquals(c, s);
  }

  @Test
  void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar scalar = Cosh.FUNCTION.apply(DecimalScalar.of(new BigDecimal("1.2356", mc), mc.getPrecision()));
    assertInstanceOf(DecimalScalar.class, scalar);
    assertEquals(scalar, DoubleScalar.of(Math.cosh(1.2356)));
  }

  @Test
  void testCosh() {
    String mathematica = "3.7621956910836314595622134777737461082939735582307116027776433475";
    Scalar x = DecimalScalar.of(BigDecimal.valueOf(2));
    Scalar s0 = Cosh.FUNCTION.apply(x);
    assertTrue(Objects.toString(s0).startsWith(mathematica.substring(0, 30)));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> Cosh.FUNCTION.apply(Quantity.of(1, "deg")));
  }

  @Test
  void testGaussScalarFail() {
    assertThrows(Throw.class, () -> Cosh.FUNCTION.apply(GaussScalar.of(6, 7)));
  }
}
