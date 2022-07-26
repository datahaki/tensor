// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.sca.Chop;

class OrthogonalMatrixQTest {
  @Test
  void testExact() {
    Tensor matrix = Tensors.fromString("{{1, 1, 1, -1}, {-1, 1, 1, 1}}").multiply(RationalScalar.of(1, 2));
    assertTrue(OrthogonalMatrixQ.of(matrix));
    OrthogonalMatrixQ.require(matrix, Chop.NONE);
  }

  @Test
  void testDetNegative() {
    Tensor matrix = DiagonalMatrix.of(-1, 1, 1, -1, -1);
    OrthogonalMatrixQ.require(matrix);
    assertEquals(Det.of(matrix), RealScalar.ONE.negate());
  }

  @Test
  void testCornerCase() {
    assertFalse(OrthogonalMatrixQ.of(RealScalar.of(1)));
    assertFalse(OrthogonalMatrixQ.of(Tensors.vector(1, 0, 0)));
    assertFalse(OrthogonalMatrixQ.of(Tensors.vector(1, 0, 2)));
    assertFalse(OrthogonalMatrixQ.of(LeviCivitaTensor.of(3)));
    assertFalse(OrthogonalMatrixQ.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  @Test
  void testRequireChop() {
    Tensor tensor = OrthogonalMatrixQ.require(IdentityMatrix.of(4), Chop.NONE);
    assertEquals(tensor, IdentityMatrix.of(4));
    assertThrows(Throw.class, () -> OrthogonalMatrixQ.require(HilbertMatrix.of(3), Chop.NONE));
  }

  @Test
  void testRequire() {
    OrthogonalMatrixQ.require(IdentityMatrix.of(4));
    assertThrows(Throw.class, () -> OrthogonalMatrixQ.require(HilbertMatrix.of(3)));
  }
}
