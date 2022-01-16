// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.N;
import junit.framework.TestCase;

public class NilpotentMatrixQTest extends TestCase {
  private static void _check(Tensor matrix) {
    assertTrue(NilpotentMatrixQ.of(matrix));
    assertTrue(NilpotentMatrixQ.of(matrix.map(N.DOUBLE)));
  }

  public void test234() {
    // Reference: https://en.wikipedia.org/wiki/Nilpotent_matrix
    _check(Tensors.fromString("{{2,-1},{4,-2}}"));
    _check(Tensors.fromString("{{2,2,-2},{5,1,-3},{1,5,-3}}"));
    _check(Tensors.fromString("{{2,2,2,-3},{6,1,1,-4},{1,6,1,-4},{1,1,6,-4}}"));
  }
}
