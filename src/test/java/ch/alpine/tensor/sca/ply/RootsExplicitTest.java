// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.ply.Roots.ComplexComparator;
import ch.alpine.tensor.sca.pow.Sqrt;

class RootsExplicitTest {
  @Test
  void testGaussScalar1() {
    Tensor coeffs = Tensors.of(GaussScalar.of(4, 7), GaussScalar.of(5, 7));
    Tensor roots = Roots.of(coeffs);
    Tensor zeros = roots.maps(Polynomial.of(coeffs));
    assertEquals(zeros, Tensors.of(GaussScalar.of(0, 7)));
    Chop.NONE.requireAllZero(zeros);
    Tolerance.CHOP.requireAllZero(zeros);
    assertEquals(roots, Tensors.of(GaussScalar.of(2, 7)));
  }

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
    Tensor zeros = roots.maps(Polynomial.of(coeffs));
    Chop._12.requireAllZero(zeros);
  }

  @Test
  void testGaussScalar() {
    Tensor coeffs = Tensors.of(GaussScalar.of(3, 7), GaussScalar.of(2, 7), GaussScalar.of(2, 7));
    Tensor roots = Roots.of(coeffs);
    Tensor zeros = roots.maps(Polynomial.of(coeffs));
    Chop.NONE.requireAllZero(zeros);
    assertEquals(roots, Tensors.of(GaussScalar.of(1, 7), GaussScalar.of(5, 7)));
  }

  @Test
  void testMatrixComp() {
    Tensor coeffs = Tensors.vector(-1, -24691356, 1);
    Polynomial polynomial = Polynomial.of(coeffs);
    Tensor r1 = polynomial.roots();
    Scalar value = RealScalar.of(-4.050000332100021E-8);
    Chop._16.requireClose(r1.Get(0), value);
    Chop._01.requireAllZero(r1.maps(polynomial));
    Tolerance.CHOP.requireZero(polynomial.apply(value));
  }

  /** @param coeffs {a, b, c} representing a + b*x + c*x^2 == 0
   * @return vector of length 2 with the roots as entries
   * if the two roots are real, then the smaller root is the first entry */
  private static Tensor simple(Tensor coeffs) {
    Scalar c = coeffs.Get(2);
    Scalar p = coeffs.Get(1).divide(c.add(c)).negate();
    Scalar q = coeffs.Get(0).divide(c).negate();
    Scalar d = Sqrt.FUNCTION.apply(p.multiply(p).add(q));
    Scalar r = d.add(p);
    // ---
    return Tensors.of(p.subtract(d), r);
  }

  @RepeatedTest(10)
  void testSimple() {
    Tensor coeffs = RandomVariate.of(ComplexNormalDistribution.STANDARD, 3);
    Tensor r1 = Roots.of(coeffs);
    Tensor r2 = Tensor.of(simple(coeffs).stream() //
        .map(Scalar.class::cast) //
        .sorted(ComplexComparator.INSTANCE));
    Chop._06.requireClose(r1, r2);
    Tensor zeros = r1.maps(Polynomial.of(coeffs));
    Chop._02.requireAllZero(zeros);
  }
}
