// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.itp.BSplineFunctionString;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Tan;

class PolynomialQTest {
  @Test
  void testSpline() {
    int length = 10;
    Tensor sample = RandomVariate.of(DiscreteUniformDistribution.of(-10, 30), length);
    int degree = 3;
    ScalarTensorFunction stf = BSplineFunctionString.of(degree, sample);
    ScalarUnaryOperator suo = s -> (Scalar) stf.apply(s);
    for (int c = 0; c < length - 1; ++c)
      assertTrue(PolynomialQ.of(suo, Clips.interval(c, c + 1), degree));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 10 })
  void testSin(int degree) {
    assertFalse(PolynomialQ.of(Sin.FUNCTION, Clips.interval(0, 3), degree));
    assertFalse(PolynomialQ.of(Cos.FUNCTION, Clips.absolute(3), degree));
    assertFalse(PolynomialQ.of(Tan.FUNCTION, Clips.absolute(3), degree));
  }

  @RepeatedTest(10)
  void testPolynomial(RepetitionInfo repetitionInfo) {
    int degree = repetitionInfo.getCurrentRepetition();
    Distribution distribution = TrapezoidalDistribution.with(0, 1, 2);
    Tensor coeffs = RandomVariate.of(distribution, degree).append(RealScalar.ONE);
    Polynomial polynomial = Polynomial.of(coeffs);
    assertTrue(PolynomialQ.of(polynomial, Clips.absoluteOne(), degree));
  }

  @Test
  void testSimple() {
    Tensor zeros = Tensors.vector(3);
    Tensor coeffs = Polynomial.fromRoots(zeros).coeffs();
    Chop.NONE.requireZero(Polynomial.of(coeffs).apply(RealScalar.of(3)));
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, zeros);
  }

  @Test
  void testChatGpt() {
    Tensor zeros = Tensors.vector(1, 2, 3);
    Tensor result = Tensors.vector(-6, 11, -6, 1);
    for (Tensor roots : Permutations.of(zeros)) {
      Tensor tensor = Polynomial.fromRoots(roots).coeffs();
      assertEquals(tensor, result);
    }
  }

  @Test
  void testQuantityD1() {
    Tensor zeros = Tensors.fromString("{3[m]}");
    Tensor coeffs = Polynomial.fromRoots(zeros).coeffs();
    Scalar result = Polynomial.of(coeffs).apply(Quantity.of(3, "m"));
    ExactScalarQ.require(result);
    assertEquals(result, Quantity.of(0, "m"));
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, zeros);
  }

  @Test
  void testQuantityD2() {
    Tensor zeros = Tensors.fromString("{3[m], 4[m]}");
    Tensor coeffs = Polynomial.fromRoots(zeros).coeffs();
    Chop.NONE.requireZero(Polynomial.of(coeffs).apply(Quantity.of(3, "m")));
    Chop.NONE.requireZero(Polynomial.of(coeffs).apply(Quantity.of(4, "m")));
    Tensor roots = Roots.of(coeffs);
    ExactTensorQ.require(roots);
    assertEquals(roots, zeros);
  }

  @Test
  void testQuantityD3() {
    Tensor zeros = Tensors.fromString("{3[m], 4[m], 6[m]}");
    Tensor coeffs = Polynomial.fromRoots(zeros).coeffs();
    assertEquals(coeffs, Tensors.fromString("{-72[m^3], 54[m^2], -13[m], 1}"));
    assertEquals(Polynomial.of(coeffs).apply(Quantity.of(3, "m")), Quantity.of(0, "m^3"));
    Chop._14.requireZero(Polynomial.of(coeffs).apply(Quantity.of(4, "m")));
    Chop._14.requireZero(Polynomial.of(coeffs).apply(Quantity.of(6, "m")));
    Tensor roots = Roots.of(coeffs);
    Chop._14.requireClose(roots, zeros);
  }

  @Test
  void testEmptyFail() {
    assertThrows(Exception.class, () -> Polynomial.fromRoots(Tensors.empty()));
  }
}
