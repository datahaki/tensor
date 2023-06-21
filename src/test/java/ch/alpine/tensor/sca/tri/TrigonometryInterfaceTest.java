// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Chop;

class TrigonometryInterfaceTest {
  private static void _check(Scalar value, ScalarUnaryOperator suo, Function<Double, Double> f) {
    Scalar scalar = UnitSystem.SI().apply(Quantity.of(value, "rad"));
    Scalar result = suo.apply(scalar);
    Scalar actual = RealScalar.of(f.apply(value.number().doubleValue()));
    assertEquals(result, actual);
  }

  @Test
  void testQuantity() {
    for (Tensor _value : Tensors.vector(-2.323, -1, -0.3, 0, 0.2, 1.2, 3., 4.456)) {
      Scalar value = (Scalar) _value;
      _check(value, Sin.FUNCTION, Math::sin);
      _check(value, Cos.FUNCTION, Math::cos);
      _check(value, Sinh.FUNCTION, Math::sinh);
      _check(value, Cosh.FUNCTION, Math::cosh);
    }
  }

  @Test
  void testQuantityDegree() {
    Scalar scalar = UnitSystem.SI().apply(Quantity.of(180, "deg"));
    Chop._13.requireClose(Sin.FUNCTION.apply(scalar), RealScalar.ZERO);
    Chop._13.requireClose(Cos.FUNCTION.apply(scalar), RealScalar.ONE.negate());
  }

  @Test
  void testNaN() {
    assertEquals(Sin.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(Cos.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(Tan.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(Cot.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(Sinh.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(Cosh.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(Tanh.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(Sinhc.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(ArcSin.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(ArcCos.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(ArcTan.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(ArcSinh.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(ArcCosh.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
    assertEquals(ArcTanh.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }

  @Test
  void testFails() {
    assertThrows(Throw.class, () -> Sin.FUNCTION.apply(Quantity.of(1.2, "m")));
    assertThrows(Throw.class, () -> Sin.FUNCTION.apply(GaussScalar.of(2, 7)));
  }
}
