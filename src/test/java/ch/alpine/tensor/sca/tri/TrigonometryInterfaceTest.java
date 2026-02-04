// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Expc;
import ch.alpine.tensor.sca.exp.Log;

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

  static List<ScalarUnaryOperator> functions() {
    return Arrays.asList( //
        Exp.FUNCTION, //
        Log.FUNCTION, //
        Expc.FUNCTION, //
        Sin.FUNCTION, //
        Cos.FUNCTION, //
        Tan.FUNCTION, //
        Cot.FUNCTION, //
        Sinh.FUNCTION, //
        Cosh.FUNCTION, //
        Tanh.FUNCTION, //
        Sinhc.FUNCTION, //
        ArcSin.FUNCTION, //
        ArcCos.FUNCTION, //
        ArcTan.FUNCTION, //
        ArcSinh.FUNCTION, //
        ArcCosh.FUNCTION, //
        ArcTanh.FUNCTION //
    );
  }

  @ParameterizedTest
  @MethodSource("functions")
  void testNaN(ScalarUnaryOperator suo) {
    assertEquals(suo.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }

  @Test
  void testDecimalCos() {
    Chop chop = Tolerance.CHOP;
    Scalar scalar = DecimalScalar.of(BigDecimal.valueOf(12.234234));
    chop.requireClose(Cos.series(scalar), Cos.FUNCTION.apply(scalar));
    chop.requireClose(Sin.series(scalar), Sin.FUNCTION.apply(scalar));
    chop.requireClose(Cosh.series(scalar), Cosh.FUNCTION.apply(scalar));
    chop.requireClose(Sinh.series(scalar), Sinh.FUNCTION.apply(scalar));
  }

  @Test
  void testFails() {
    assertThrows(Throw.class, () -> Sin.FUNCTION.apply(Quantity.of(1.2, "m")));
    assertThrows(Throw.class, () -> Sin.FUNCTION.apply(GaussScalar.of(2, 7)));
  }
}
