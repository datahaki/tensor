// code by jph
package ch.alpine.tensor.mat.pi;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class LeastAbsoluteDeviationsTest {
  private static Scalar error(Tensor A, Tensor x, Tensor b) {
    return Vector1Norm.of(A.dot(x).subtract(b));
  }

  @Test
  void testSimple() {
    Distribution distribution = TriangularDistribution.with(0, 1);
    Tensor matrix = RandomVariate.of(distribution, 8, 4);
    Tensor b = RandomVariate.of(distribution, 8);
    Tensor x0 = LeastSquares.of(matrix, b);
    error(matrix, x0, b);
    // System.out.println(error(matrix, x0, b));
    // System.out.println(error(matrix, LeastAbsoluteDeviations.of(matrix, b, RealScalar.of(0.001)), b));
    // System.out.println(error(matrix, LeastAbsoluteDeviations.of(matrix, b, RealScalar.of(0.01)), b));
    // System.out.println(error(matrix, LeastAbsoluteDeviations.of(matrix, b, RealScalar.of(0.1)), b));
    // System.out.println(error(matrix, LeastAbsoluteDeviations.of(matrix, b, RealScalar.of(1)), b));
    // System.out.println(error(matrix, LeastAbsoluteDeviations.of(matrix, b, RealScalar.of(10)), b));
  }

  @Test
  void testExact() {
    Distribution distribution = UniformDistribution.of(0, 2);
    Tensor A = RandomVariate.of(distribution, 20, 4);
    Tensor b = A.dot(RandomVariate.of(distribution, 4));
    Tensor x = LeastAbsoluteDeviations.of(A, b, RealScalar.ONE);
    Tolerance.CHOP.requireAllZero(A.dot(x).subtract(b));
  }

  @Test
  void testApprox() {
    Distribution distribution = UniformDistribution.of(0, 2);
    Tensor A = RandomVariate.of(distribution, 20, 10);
    Tensor b = RandomVariate.of(distribution, 20);
    Tensor x1 = LeastAbsoluteDeviations.of(A, b, RealScalar.of(.1));
    Tensor x2 = LeastSquares.of(A, b);
    Tensor e1 = A.dot(x1).subtract(b);
    Tensor e2 = A.dot(x2).subtract(b);
    VectorQ.require(e1);
    VectorQ.require(e2);
    // IO.println(Vector1Norm.of(e1));
    // IO.println(Vector1Norm.of(e2));
    // IO.println(Vector2Norm.of(e1));
    // IO.println(Vector2Norm.of(e2));
  }
}
