// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
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
    Tensor matrix = Tensors.fromString("{{1, 1, 1, -1}, {-1, 1, 1, 1}}").multiply(Rational.of(1, 2));
    assertTrue(OrthogonalMatrixQ.INSTANCE.test(matrix));
    new OrthogonalMatrixQ(Chop.NONE).require(matrix);
  }

  @Test
  void testDetNegative() {
    Tensor matrix = DiagonalMatrix.of(-1, 1, 1, -1, -1);
    OrthogonalMatrixQ.INSTANCE.require(matrix);
    assertEquals(Det.of(matrix), RealScalar.ONE.negate());
  }

  @Test
  void testCornerCase() {
    assertFalse(OrthogonalMatrixQ.INSTANCE.test(RealScalar.of(1)));
    assertFalse(OrthogonalMatrixQ.INSTANCE.test(Tensors.vector(1, 0, 0)));
    assertFalse(OrthogonalMatrixQ.INSTANCE.test(Tensors.vector(1, 0, 2)));
    assertFalse(OrthogonalMatrixQ.INSTANCE.test(LeviCivitaTensor.of(3)));
    assertFalse(OrthogonalMatrixQ.INSTANCE.test(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }

  @Test
  void testRequireChop() {
    Tensor tensor = new OrthogonalMatrixQ(Chop.NONE).require(IdentityMatrix.of(4));
    assertEquals(tensor, IdentityMatrix.of(4));
    assertThrows(Throw.class, () -> new OrthogonalMatrixQ(Chop.NONE).require(HilbertMatrix.of(3)));
  }

  @Test
  void testRequire() {
    OrthogonalMatrixQ.INSTANCE.require(IdentityMatrix.of(4));
    assertThrows(Throw.class, () -> OrthogonalMatrixQ.INSTANCE.require(HilbertMatrix.of(3)));
  }
}
