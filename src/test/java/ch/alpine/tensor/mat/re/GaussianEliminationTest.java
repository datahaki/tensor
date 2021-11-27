// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.usr.AssertFail;
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

  public void testRecreateError1() {
    AssertFail.of(() -> LinearSolve.of(HilbertMatrix.of(4, 5), UnitVector.of(4, 3)));
    AssertFail.of(() -> LinearSolve.of(HilbertMatrix.of(5, 4), UnitVector.of(4, 3)));
  }

  public void testRecreateError2() {
    AssertFail.of(() -> LinearSolve.of(HilbertMatrix.of(4, 4), UnitVector.of(5, 3)));
  }
}
