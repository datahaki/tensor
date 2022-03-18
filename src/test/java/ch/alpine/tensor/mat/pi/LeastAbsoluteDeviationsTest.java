// code by jph
package ch.alpine.tensor.mat.pi;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TriangularDistribution;

public class LeastAbsoluteDeviationsTest {
  private static Scalar error(Tensor A, Tensor x, Tensor b) {
    return Vector1Norm.of(A.dot(x).subtract(b));
  }

  @Test
  public void testSimple() {
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
}
