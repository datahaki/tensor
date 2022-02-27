// code by jph
package ch.alpine.tensor.fft;

import java.util.Arrays;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.mat.GaussianMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class FullCorrelateTest extends TestCase {
  public void testSimple() {
    Tensor res1 = FullCorrelate.of(Tensors.vector(3, 7, 2), Tensors.vector(1, 2, 3, 8, 9));
    Tensor res2 = FullCorrelate.of(Tensors.vector(1, 2, 3, 8, 9), Tensors.vector(3, 7, 2));
    assertEquals(res1, Reverse.of(res2));
  }

  public void testReverse1() {
    Tensor res1 = FullCorrelate.of(Reverse.of(Tensors.vector(3, 7, 2)), Tensors.vector(1, 2, 3, 8, 9));
    Tensor res2 = FullCorrelate.of(Reverse.of(Tensors.vector(1, 2, 3, 8, 9)), Tensors.vector(3, 7, 2));
    assertEquals(res1, res2);
  }

  public void testReverse2() {
    Tensor res1 = FullCorrelate.of(Tensors.vector(3, 7, 2), Reverse.of(Tensors.vector(1, 2, 3, 8, 9)));
    Tensor res2 = FullCorrelate.of(Tensors.vector(1, 2, 3, 8, 9), Reverse.of(Tensors.vector(3, 7, 2)));
    assertEquals(res1, res2);
  }

  public void testRank3() {
    Tensor a = HilbertMatrix.of(2, 3);
    Tensor b = GaussianMatrix.of(2); // 5x5
    Tensor result = FullCorrelate.of(a, b);
    assertEquals(Dimensions.of(result), Arrays.asList(6, 7));
  }
}
