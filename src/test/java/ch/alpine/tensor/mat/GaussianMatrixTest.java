// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Reverse;

/** [
 * [ 0.0113 0.0838 0.0113 ]
 * [ 0.0838 0.6193 0.0838 ]
 * [ 0.0113 0.0838 0.0113 ]
 * ] */
class GaussianMatrixTest {
  @RepeatedTest(4)
  void testSmall(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = GaussianMatrix.of(n);
    int size = 2 * n + 1;
    assertEquals(Dimensions.of(matrix), Arrays.asList(size, size));
    assertTrue(SymmetricMatrixQ.INSTANCE.test(matrix));
    assertEquals(Reverse.of(matrix), matrix);
  }

  @Test
  void testFail() {
    assertThrows(ArithmeticException.class, () -> GaussianMatrix.of(0));
    assertThrows(IllegalArgumentException.class, () -> GaussianMatrix.of(-1));
  }
}
