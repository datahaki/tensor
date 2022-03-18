// code by jph
package ch.alpine.tensor.sca.tri;

import java.util.function.Function;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TrigonometryInterfaceTest extends TestCase {
  private static void _check(Scalar value, ScalarUnaryOperator suo, Function<Double, Double> f) {
    Scalar scalar = UnitSystem.SI().apply(Quantity.of(value, "rad"));
    Scalar result = suo.apply(scalar);
    Scalar actual = RealScalar.of(f.apply(value.number().doubleValue()));
    assertEquals(result, actual);
  }

  public void testQuantity() {
    for (Tensor _value : Tensors.vector(-2.323, -1, -0.3, 0, 0.2, 1.2, 3., 4.456)) {
      Scalar value = (Scalar) _value;
      _check(value, Sin::of, Math::sin);
      _check(value, Cos::of, Math::cos);
      _check(value, Sinh::of, Math::sinh);
      _check(value, Cosh::of, Math::cosh);
    }
  }

  public void testQuantityDegree() {
    Scalar scalar = UnitSystem.SI().apply(Quantity.of(180, "deg"));
    Chop._13.requireClose(Sin.of(scalar), RealScalar.ZERO);
    Chop._13.requireClose(Cos.of(scalar), RealScalar.ONE.negate());
  }

  public void testNaN() {
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

  public void testFails() {
    AssertFail.of(() -> Sin.of(Quantity.of(1.2, "m")));
    AssertFail.of(() -> Sin.of(GaussScalar.of(2, 7)));
  }
}
