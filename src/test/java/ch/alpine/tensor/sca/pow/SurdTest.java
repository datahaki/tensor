// code by jph
package ch.alpine.tensor.sca.pow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;

class SurdTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator surd = Surd.of(3);
    Scalar scalar = CubeRoot.FUNCTION.apply(RealScalar.of(27));
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(3));
    Tolerance.CHOP.requireClose(scalar, surd.apply(RealScalar.of(27)));
  }

  @Test
  void testQuantity() {
    Scalar input = Quantity.of(2, "m^3");
    Scalar scalar = Surd.of(3).apply(input);
    Tolerance.CHOP.requireClose(scalar, Quantity.of(1.2599210498948732, "m"));
    Tolerance.CHOP.requireClose(Times.of(scalar, scalar, scalar), input);
  }

  @Test
  void testNegative() {
    Scalar input = Quantity.of(-2, "m^3");
    Scalar scalar = Surd.of(3).apply(input);
    Tolerance.CHOP.requireClose(scalar, Quantity.of(-1.2599210498948731648, "m"));
  }

  @Test
  void testZero() {
    for (int exp = 1; exp < 10; ++exp) {
      Scalar scalar = Surd.of(exp).apply(RealScalar.ZERO);
      ExactScalarQ.require(scalar);
      assertEquals(scalar, RealScalar.ZERO);
    }
  }

  @Test
  void testNegativeExp() {
    Scalar scalar = Surd.of(-1).apply(RealScalar.of(4));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Rational.of(1, 4));
    assertEquals(Surd.of(-2).apply(RealScalar.of(4)), Rational.of(1, 2));
  }

  @Test
  void testOf() {
    ScalarUnaryOperator suo = Surd.of(3);
    Tensor tensor = Tensors.vector(-27, -8, -1, 0, 1, 8, 27).maps(suo);
    assertEquals(tensor, Range.of(-3, 4));
    assertEquals(suo.toString(), "Surd[3]");
  }

  @Test
  void testNegativeN() {
    ScalarUnaryOperator suo = Surd.of(-3);
    assertEquals(suo.toString(), "Surd[-3]");
    Tolerance.CHOP.requireClose(suo.apply(Rational.of(1, 27)), RealScalar.of(3));
  }

  @Test
  void testNNegativeTwo() {
    ScalarUnaryOperator suo = Surd.of(-2);
    assertEquals(suo.toString(), "Surd[-2]");
    Scalar scalar = suo.apply(Rational.of(9, 16));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Rational.of(4, 3));
  }

  @Test
  void testComplexFail() {
    Scalar scalar = ComplexScalar.of(12, 23);
    assertThrows(ClassCastException.class, () -> Surd.of(3).apply(scalar));
  }

  @Test
  void testZeroExpFail() {
    assertThrows(ArithmeticException.class, () -> Surd.of(0));
  }
}
