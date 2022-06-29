// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.N;

class NilpotentMatrixQTest {
  private static void _check(Tensor matrix) {
    assertTrue(NilpotentMatrixQ.of(matrix));
    assertTrue(NilpotentMatrixQ.of(matrix.map(N.DOUBLE)));
  }

  @Test
  void test234() {
    // Reference: https://en.wikipedia.org/wiki/Nilpotent_matrix
    _check(Tensors.fromString("{{2,-1},{4,-2}}"));
    _check(Tensors.fromString("{{2,2,-2},{5,1,-3},{1,5,-3}}"));
    _check(Tensors.fromString("{{2,2,2,-3},{6,1,1,-4},{1,6,1,-4},{1,1,6,-4}}"));
  }

  @Test
  void testNope() {
    assertFalse(NilpotentMatrixQ.of(IdentityMatrix.of(3)));
    assertFalse(NilpotentMatrixQ.of(DiagonalMatrix.of(3, 0)));
    assertFalse(NilpotentMatrixQ.of(DiagonalMatrix.of(3, -3)));
  }
}
