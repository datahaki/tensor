// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.tri.ArcTan;

class ArgTest {
  @Test
  void testArg() {
    Scalar scalar = ComplexScalar.of(RationalScalar.of(-2, 3), RationalScalar.of(-5, 100));
    assertEquals(Arg.of(scalar).toString(), "-3.066732805879026");
    assertEquals(Arg.of(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(Arg.of(DoubleScalar.of(0.2)), RealScalar.ZERO);
    assertEquals(Arg.of(DoubleScalar.of(-1)), DoubleScalar.of(Math.PI));
    assertEquals(Arg.of(RationalScalar.of(-1, 3)), DoubleScalar.of(Math.PI));
  }

  @Test
  void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    assertEquals(Arg.of(DecimalScalar.of(new BigDecimal("3.14", mc), mc.getPrecision())), RealScalar.ZERO);
    assertEquals(Arg.of(DecimalScalar.of(new BigDecimal("-112.14", mc), mc.getPrecision())), RealScalar.of(Math.PI));
  }

  @Test
  void testQuantity() {
    Unit unit = Unit.of("s*m^3");
    Scalar s = Quantity.of(ComplexScalar.of(3, 4), unit);
    Scalar a = Arg.of(s);
    Scalar b = ArcTan.of(RealScalar.of(3), RealScalar.of(4));
    assertEquals(a, b);
  }

  @Test
  void testNaN() {
    assertEquals(Arg.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }

  @Test
  void testQuaternionFail() {
    assertThrows(TensorRuntimeException.class, () -> Arg.FUNCTION.apply(Quaternion.of(1, 2, 3, 4)));
  }

  @Test
  void testGaussScalarFail() {
    Scalar scalar = GaussScalar.of(1, 7);
    assertThrows(TensorRuntimeException.class, () -> Arg.of(scalar));
  }
}
