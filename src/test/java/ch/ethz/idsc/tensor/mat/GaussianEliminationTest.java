// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class GaussianEliminationTest extends TestCase {
  public void testPackageVisibility() {
    int modifiers = GaussianElimination.class.getModifiers();
    assertEquals(modifiers & 0x1, 0x1); // non public but package
  }

  public void testPivots() {
    Tensor matrix = HilbertMatrix.of(3);
    Tensor rhs = Tensors.vector(-1, -2, 3);
    Tensor ge1 = GaussianElimination.of(matrix, rhs, Pivots.ARGMAX_ABS);
    Tensor ge2 = GaussianElimination.of(matrix, rhs, Pivots.FIRST_NON_ZERO);
    assertEquals(ge1, ge2);
    ExactTensorQ.require(ge1);
    ExactTensorQ.require(ge2);
    assertEquals(ge1, Tensors.vector(153, -888, 870));
  }

  public void testDeterminant() {
    for (int n = 4; n < 7; ++n) {
      Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-20, 100), n, n);
      GaussianElimination gaussianElimination = //
          new GaussianElimination(matrix, IdentityMatrix.of(n), Pivots.ARGMAX_ABS);
      gaussianElimination.det();
      assertEquals(Det.of(matrix), gaussianElimination.det());
      gaussianElimination.solve();
      assertEquals(Inverse.of(matrix), gaussianElimination.solve());
    }
  }
}
