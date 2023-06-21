// code by jph
package ch.alpine.tensor.sca.pow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Rationalize;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.AbsSquared;

class SqrtTest {
  @Test
  void testNegative() {
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(RealScalar.of(10000));
    Scalar n2 = RealScalar.of(-2);
    Scalar sr = Sqrt.FUNCTION.apply(n2);
    assertEquals(suo.apply(AbsSquared.FUNCTION.apply(sr)), RealScalar.of(2));
    assertEquals(suo.apply(sr.multiply(sr)), n2);
  }

  @Test
  void testMixingTemplates() {
    {
      Scalar tensor = RealScalar.of(-2);
      Sqrt.FUNCTION.apply(tensor);
      Scalar scalar = Sqrt.FUNCTION.apply(tensor);
      scalar.zero();
    }
    {
      Scalar tensor = RationalScalar.of(-2, 3);
      Sqrt.FUNCTION.apply(tensor);
      Scalar scalar = Sqrt.FUNCTION.apply(tensor);
      scalar.zero();
    }
  }

  @Test
  void testComplex() {
    Scalar scalar = ComplexScalar.of(0, 2);
    Scalar root = Sqrt.FUNCTION.apply(scalar);
    Scalar res = ComplexScalar.of(1, 1);
    Tolerance.CHOP.requireClose(root, res);
  }

  @Test
  void testZero() {
    assertEquals(RealScalar.ZERO, Sqrt.FUNCTION.apply(RealScalar.ZERO));
  }

  @Test
  void testRational() {
    assertEquals(Sqrt.FUNCTION.apply(RationalScalar.of(16, 25)).toString(), "4/5");
    Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(-16, 25));
    assertInstanceOf(ComplexScalar.class, scalar);
    assertEquals(scalar.toString(), "4/5*I");
  }

  @Test
  void testReal() {
    assertEquals(Sqrt.FUNCTION.apply(RationalScalar.of(-16, 25)).toString(), "4/5*I");
    assertEquals(Sqrt.FUNCTION.apply(RealScalar.of(16 / 25.)), Scalars.fromString("4/5"));
    assertEquals(Sqrt.FUNCTION.apply(RealScalar.of(-16 / 25.)), Scalars.fromString("4/5*I"));
  }

  @Test
  void testTensor() {
    Tensor vector = Tensors.vector(1, 4, 9, 16).map(Sqrt.FUNCTION);
    assertEquals(vector, Tensors.vector(1, 2, 3, 4));
  }

  @Test
  void testPositiveInfty() {
    assertEquals( //
        Sqrt.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY), //
        DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testNegativeInfty() {
    assertEquals( //
        Sqrt.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY), //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(9, "m^2");
    Scalar qs2 = Quantity.of(3, "m");
    assertEquals(Sqrt.FUNCTION.apply(qs1), qs2);
  }

  @Test
  void testQuantity2() {
    Scalar qs1 = Quantity.of(9, "m*s^2");
    Scalar qs2 = Quantity.of(3, "m^1/2*s");
    assertEquals(Sqrt.FUNCTION.apply(qs1), qs2);
  }

  @Test
  void testNaN() {
    assertEquals(Sqrt.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }

  @Test
  void testFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Throw.class, () -> Sqrt.FUNCTION.apply(scalar));
  }
}
