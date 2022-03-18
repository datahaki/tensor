// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

public class CoshTest {
  @Test
  public void testReal() {
    Scalar c = Cosh.FUNCTION.apply(RealScalar.of(2));
    Scalar s = DoubleScalar.of(Math.cosh(2));
    Scalar t = Cosh.of(RealScalar.of(2));
    assertEquals(c, s);
    assertEquals(c, t);
  }

  @Test
  public void testComplex() {
    Scalar c = Cosh.of(ComplexScalar.of(2, 3.));
    // -3.72455 + 0.511823 I
    Scalar s = Scalars.fromString("-3.7245455049153224+0.5118225699873846*I");
    assertEquals(c, s);
  }

  @Test
  public void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar scalar = Cosh.of(DecimalScalar.of(new BigDecimal("1.2356", mc), mc.getPrecision()));
    assertTrue(scalar instanceof DecimalScalar);
    assertEquals(scalar, DoubleScalar.of(Math.cosh(1.2356)));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Cosh.of(Quantity.of(1, "deg")));
  }

  @Test
  public void testGaussScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Cosh.of(GaussScalar.of(6, 7)));
  }
}
