// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.mat.GaussianMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;

public class FullCorrelateTest {
  @Test
  public void testSimple() {
    Tensor res1 = FullCorrelate.of(Tensors.vector(3, 7, 2), Tensors.vector(1, 2, 3, 8, 9));
    Tensor res2 = FullCorrelate.of(Tensors.vector(1, 2, 3, 8, 9), Tensors.vector(3, 7, 2));
    assertEquals(res1, Reverse.of(res2));
  }

  @Test
  public void testReverse1() {
    Tensor res1 = FullCorrelate.of(Reverse.of(Tensors.vector(3, 7, 2)), Tensors.vector(1, 2, 3, 8, 9));
    Tensor res2 = FullCorrelate.of(Reverse.of(Tensors.vector(1, 2, 3, 8, 9)), Tensors.vector(3, 7, 2));
    assertEquals(res1, res2);
  }

  @Test
  public void testReverse2() {
    Tensor res1 = FullCorrelate.of(Tensors.vector(3, 7, 2), Reverse.of(Tensors.vector(1, 2, 3, 8, 9)));
    Tensor res2 = FullCorrelate.of(Tensors.vector(1, 2, 3, 8, 9), Reverse.of(Tensors.vector(3, 7, 2)));
    assertEquals(res1, res2);
  }

  @Test
  public void testRank3() {
    Tensor a = HilbertMatrix.of(2, 3);
    Tensor b = GaussianMatrix.of(2); // 5x5
    Tensor result = FullCorrelate.of(a, b);
    assertEquals(Dimensions.of(result), Arrays.asList(6, 7));
  }
}