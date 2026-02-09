// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.lie.rot.Quaternion;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

class AbsSquaredTest {
  @Test
  void testAbsAndSquared() {
    Tensor tensor = Tensors.of( //
        Quaternion.of(1, 2, 3, 4), //
        RationalScalar.HALF, RationalScalar.of(2, 7), RealScalar.TWO, //
        ComplexScalar.I, ComplexScalar.of(2, 3), ComplexScalar.of(2.0, 3.3), //
        RealScalar.of(-3), Pi.VALUE, Pi.HALF.negate(), //
        Quantity.of(2.3, "m*s^-3"), Quantity.of(ComplexScalar.of(2, 3), "m^2*s^-1"));
    for (Tensor _q : tensor) {
      Scalar q = (Scalar) _q;
      Scalar abs = Abs.FUNCTION.apply(q);
      Tolerance.CHOP.requireClose(AbsSquared.FUNCTION.apply(q), abs.multiply(abs));
    }
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Scalars.fromString("3+4*I[s^2*m^-1]");
    Scalar qs2 = AbsSquared.FUNCTION.apply(qs1);
    assertEquals(qs2.toString(), "25[m^-2*s^4]");
  }

  @Test
  void testTensor() {
    Tensor qs1 = Tensors.fromString("{3+4*I[s^2*m^-1]}");
    Tensor qs2 = qs1.maps(AbsSquared.FUNCTION);
    ExactTensorQ.require(qs2);
    assertEquals(qs2.toString(), "{25[m^-2*s^4]}");
  }

  @Test
  void testBetween() {
    assertEquals(AbsSquared.between(RealScalar.of(101), RealScalar.of(103)), RealScalar.of(4));
    assertEquals(AbsSquared.between(RealScalar.of(104), RealScalar.of(101)), RealScalar.of(9));
  }

  @Test
  void testNonConjugate() {
    GaussScalar a = GaussScalar.of(3, 12347);
    GaussScalar b = GaussScalar.of(3962, 12347);
    Scalar scalar = b.subtract(a);
    assertEquals(scalar.multiply(scalar), GaussScalar.of(3959 * 3959, 12347));
  }

  @Test
  void testInfinity() {
    assertEquals(AbsSquared.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(AbsSquared.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testGaussScalar() {
    assertEquals(AbsSquared.FUNCTION.apply(GaussScalar.of(2, 3)), GaussScalar.of(-5, 3));
  }

  @Test
  void testStringFail() {
    assertThrows(Throw.class, () -> AbsSquared.FUNCTION.apply(StringScalar.of("idsc")));
  }
}
