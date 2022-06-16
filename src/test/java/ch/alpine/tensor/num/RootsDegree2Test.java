// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.sca.Chop;

class RootsDegree2Test {
  @Test
  void testZerosQuantity() {
    Tensor roots = Roots.of(Tensors.fromString("{0, 0, 1[m^-2], 0[m^-3]}"));
    assertEquals(roots, Tensors.fromString("{0[m^2], 0[m^2]}"));
  }

  @Test
  void testQuadraticQuantity() {
    Tensor coeffs = Tensors.fromString("{21, - 10 [s^-1], +1 [s^-2], 0, 0, 0}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, Tensors.fromString("{3[s], 7[s]}"));
    ExactTensorQ.require(roots);
  }

  @Test
  void testQuadraticComplexQuantity() {
    Tensor coeffs = Tensors.fromString("{1, 0 [s^-1], 1 [s^-2]}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, Tensors.fromString("{-I[s], I[s]}"));
    ExactTensorQ.require(roots);
  }

  @Test
  void testPseudoCubicQuantity() {
    Tensor coeffs = Tensors.fromString("{0, 21, - 10 [s^-1], +1 [s^-2], 0, 0, 0}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(Sort.of(roots), Tensors.fromString("{0[s], 3[s], 7[s]}"));
    ExactTensorQ.require(roots);
  }

  @Test
  void testPseudoQuarticQuantity() {
    Tensor coeffs = Tensors.fromString("{0, 0, 21, - 10 [s^-1], +1 [s^-2], 0, 0, 0}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(Sort.of(roots), Tensors.fromString("{0[s], 0[s], 3[s], 7[s]}"));
    ExactTensorQ.require(roots);
  }

  @Test
  void testChallenge() {
    Tensor coeffs = Tensors.vector(-0.45461391407082863, -0.44401256484994056, -0.43798541191338114);
    Tensor roots = Roots.of(coeffs);
    Tensor zeros = roots.map(Polynomial.of(coeffs));
    Chop._12.requireAllZero(zeros);
  }

  @Test
  void testGaussScalar() {
    Tensor coeffs = Tensors.of(GaussScalar.of(3, 7), GaussScalar.of(2, 7), GaussScalar.of(2, 7));
    Tensor roots = Roots.of(coeffs);
    Tensor zeros = roots.map(Polynomial.of(coeffs));
    Chop.NONE.requireAllZero(zeros);
    assertEquals(roots, Tensors.of(GaussScalar.of(1, 7), GaussScalar.of(5, 7)));
  }
}
