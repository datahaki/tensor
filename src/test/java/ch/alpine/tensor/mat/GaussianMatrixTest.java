// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Reverse;

/** [
 * [ 0.0113 0.0838 0.0113 ]
 * [ 0.0838 0.6193 0.0838 ]
 * [ 0.0113 0.0838 0.0113 ]
 * ] */
public class GaussianMatrixTest {
  private static void _check(int n) {
    Tensor matrix = GaussianMatrix.of(n);
    int size = 2 * n + 1;
    assertEquals(Dimensions.of(matrix), Arrays.asList(size, size));
    assertTrue(SymmetricMatrixQ.of(matrix));
    assertEquals(Reverse.of(matrix), matrix);
  }

  @Test
  public void testSmall() {
    for (int index = 1; index < 5; ++index)
      _check(index);
  }

  @Test
  public void testFail() {
    assertThrows(ArithmeticException.class, () -> GaussianMatrix.of(0));
    assertThrows(IllegalArgumentException.class, () -> GaussianMatrix.of(-1));
  }
}
