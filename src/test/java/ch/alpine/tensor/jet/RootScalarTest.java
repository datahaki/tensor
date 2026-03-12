// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class RootScalarTest {
  @Test
  void testSimple() {
    RootScalar r1 = new RootScalar(RealScalar.of(-7), RealScalar.of(3), RealScalar.of(2));
    RootScalar r2 = new RootScalar(RealScalar.of(+8), RealScalar.of(6), RealScalar.of(2));
    assertEquals(r1.add(r2), new RootScalar(RealScalar.of(1), RealScalar.of(9), RealScalar.of(2)));
    assertEquals(r1.multiply(r2), new RootScalar(RealScalar.of(-20), RealScalar.of(-18), RealScalar.of(2)));
    assertEquals(r1.subtract(r1), RealScalar.ZERO);
  }

  @Test
  void testReciprocal() {
    RootScalar rootScalar = new RootScalar(Rational.of(-7, 13), Rational.of(3, -11), RealScalar.of(2));
    assertEquals(rootScalar.multiply(rootScalar.reciprocal()), RealScalar.ONE);
  }

  @Test
  void testNumber() {
    RootScalar rootScalar = new RootScalar(Rational.of(-7, 5), Rational.of(-3, 17), RealScalar.of(3));
    assertThrows(Throw.class, rootScalar::number);
  }

  @Test
  void testReal() {
    RootScalar rootScalar = new RootScalar(Rational.of(-7, 5), Rational.of(-3, 17), RealScalar.of(3));
    Number number = rootScalar.explicit().number();
    double value = -7 / 5.0 - 3 / 17.0 * Math.sqrt(3);
    Tolerance.CHOP.requireClose(RealScalar.of(number), RealScalar.of(value));
  }

  @Test
  void testZero() {
    RootScalar rootScalar = new RootScalar(Rational.of(-2, 13), Rational.of(-8, 11), RealScalar.of(2));
    Scalar zero = rootScalar.zero();
    assertEquals(zero, RealScalar.ZERO);
  }

  @Test
  void testEquations() {
    Distribution distribution = DiscreteUniformDistribution.of(-10000, 10000);
    Scalar ba = RealScalar.of(2);
    Tensor matrix = //
        Tensors.matrix((_, _) -> new RootScalar(RandomVariate.of(distribution), RandomVariate.of(distribution), ba), 7, 7);
    Tensor rhs = //
        Tensors.matrix((_, _) -> new RootScalar(RandomVariate.of(distribution), RandomVariate.of(distribution), ba), 7, 3);
    Tensor sol = LinearSolve.of(matrix, rhs);
    assertEquals(matrix.dot(sol), rhs);
  }

  @Test
  void testInverse() {
    Distribution distribution = DiscreteUniformDistribution.of(-3000, 3000);
    Scalar ba = RealScalar.of(2);
    Tensor matrix = //
        Tensors.matrix((_, _) -> new RootScalar(RandomVariate.of(distribution), RandomVariate.of(distribution), ba), 5, 5);
    Tensor inv = Inverse.of(matrix);
    assertEquals(matrix.dot(inv), IdentityMatrix.of(5));
  }
}
