// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class NormalMatrixQTest {
  @Test
  void testSimple() {
    Tensor matrix = Tensors.fromString("{{5 + I, -2*I}, {2, 4 + 2*I}}");
    assertTrue(NormalMatrixQ.of(matrix));
  }

  @Test
  void testRequire() {
    Tensor matrix = Tensors.fromString("{{1, 2, -1}, {-1, 1, 2}, {2, -1, 1}}");
    NormalMatrixQ.require(matrix);
  }

  @Test
  void testNope() {
    assertFalse(NormalMatrixQ.of(Pi.VALUE));
    assertFalse(NormalMatrixQ.of(Tensors.vector(1, 2, 3)));
    assertFalse(NormalMatrixQ.of(HilbertMatrix.of(2, 3)));
  }
}
