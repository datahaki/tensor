// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;

class AntihermitianMatrixQTest {
  @Test
  void testSimple() {
    assertTrue(AntihermitianMatrixQ.INSTANCE.test(Array.zeros(2, 2)));
    assertFalse(AntihermitianMatrixQ.INSTANCE.test(HilbertMatrix.of(3)));
  }

  @Test
  void test2x2() {
    Tensor matrix = Tensors.fromString("{{0,1+2*I},{-1+2*I,0}}");
    assertEquals(AntihermitianMatrixQ.INSTANCE.require(matrix), matrix);
  }

  @Test
  void testRequireFail() {
    Tensor matrix = Tensors.fromString("{{1,1+2*I},{-1+2*I,0}}");
    assertThrows(Throw.class, () -> AntihermitianMatrixQ.INSTANCE.require(matrix));
  }
}
