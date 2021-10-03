// code by jph
package ch.alpine.tensor;

import java.util.Arrays;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TensorBlockTest extends TestCase {
  public void testBlock() {
    Tensor a = Tensors.vector(1, 2, 3, 4, 5, 6);
    assertEquals(a.block(Arrays.asList(2), Arrays.asList(2)), Tensors.vector(3, 4));
    AssertFail.of(() -> a.block(Arrays.asList(1), Arrays.asList(2, 1)));
  }

  public void testNonRefs() {
    Tensor a = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor b = a.block(Arrays.asList(2), Arrays.asList(3));
    b.set(Array.zeros(3), Tensor.ALL);
    assertEquals(a, Tensors.vector(1, 2, 0, 0, 0, 6));
  }

  public void testRefs2d() {
    Tensor a = HilbertMatrix.of(5, 6);
    Tensor b = a.block(Arrays.asList(1, 2), Arrays.asList(3, 4));
    b.set(Array.zeros(3, 4), Tensor.ALL, Tensor.ALL);
    Tensor expect = Tensors.fromString( //
        "{{1, 1/2, 1/3, 1/4, 1/5, 1/6}, {1/2, 1/3, 0, 0, 0, 0}, {1/3, 1/4, 0, 0, 0, 0}, {1/4, 1/5, 0, 0, 0, 0}, {1/5, 1/6, 1/7, 1/8, 1/9, 1/10}}");
    assertEquals(a, expect);
  }
}
