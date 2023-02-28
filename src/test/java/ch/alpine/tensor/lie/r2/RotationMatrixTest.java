// code by jph
package ch.alpine.tensor.lie.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;

class RotationMatrixTest {
  @Test
  void testPointThree() {
    Tensor matrix = RotationMatrix.of(RealScalar.of(0.3));
    assertEquals(IdentityMatrix.of(2), MatrixDotTranspose.of(matrix, matrix));
    assertTrue(OrthogonalMatrixQ.of(matrix));
    assertTrue(matrix.Get(0, 1).toString().startsWith("-0.2955"));
    assertTrue(matrix.Get(1, 1).toString().startsWith("0.95533"));
  }

  @Test
  void testComplex() {
    Tensor matrix = RotationMatrix.of(ComplexScalar.of(1, 2));
    Tolerance.CHOP.requireClose(matrix.Get(0, 0), ComplexScalar.of(2.0327230070196655294, -3.0518977991518000575));
    Tolerance.CHOP.requireClose(matrix.Get(0, 1), ComplexScalar.of(-3.1657785132161681467, -1.9596010414216058971));
    assertTrue(OrthogonalMatrixQ.of(matrix));
  }

  @Test
  void testNumber() {
    Tensor matrix = RotationMatrix.of(0.2);
    assertFalse(Tolerance.CHOP.isClose(matrix, IdentityMatrix.of(2)));
    Tolerance.CHOP.requireClose(matrix.dot(RotationMatrix.of(-0.2)), IdentityMatrix.of(2));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> RotationMatrix.of(GaussScalar.of(2, 7)));
  }
}
