// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;

public class OrthogonalMatrixQTest {
  @Test
  public void testExact() {
    Tensor matrix = Tensors.fromString("{{1, 1, 1, -1}, {-1, 1, 1, 1}}").multiply(RationalScalar.of(1, 2));
    assertTrue(OrthogonalMatrixQ.of(matrix));
    OrthogonalMatrixQ.require(matrix, Chop.NONE);
  }

  @Test
  public void testDetNegative() {
    Tensor matrix = DiagonalMatrix.of(-1, 1, 1, -1, -1);
    OrthogonalMatrixQ.require(matrix);
    assertEquals(Det.of(matrix), RealScalar.ONE.negate());
  }

  @Test
  public void testCornerCase() {
    assertFalse(OrthogonalMatrixQ.of(RealScalar.of(1)));
    assertFalse(OrthogonalMatrixQ.of(Tensors.vector(1, 0, 0)));
    assertFalse(OrthogonalMatrixQ.of(Tensors.vector(1, 0, 2)));
    assertFalse(OrthogonalMatrixQ.of(LeviCivitaTensor.of(3)));
    assertFalse(OrthogonalMatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  @Test
  public void testRequireChop() {
    OrthogonalMatrixQ.require(IdentityMatrix.of(4), Chop.NONE);
    AssertFail.of(() -> OrthogonalMatrixQ.require(HilbertMatrix.of(3), Chop.NONE));
  }

  @Test
  public void testRequire() {
    OrthogonalMatrixQ.require(IdentityMatrix.of(4));
    AssertFail.of(() -> OrthogonalMatrixQ.require(HilbertMatrix.of(3)));
  }
}
