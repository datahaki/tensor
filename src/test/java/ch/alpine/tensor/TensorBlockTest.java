// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.HilbertMatrix;

class TensorBlockTest {
  @Test
  void testBlock() {
    Tensor a = Tensors.vector(1, 2, 3, 4, 5, 6);
    assertEquals(a.block(Arrays.asList(2), Arrays.asList(2)), Tensors.vector(3, 4));
    assertThrows(IllegalArgumentException.class, () -> a.block(Arrays.asList(1), Arrays.asList(2, 1)));
  }

  @Test
  void testBlock2() {
    Tensor array = Array.zeros(5, 5);
    Tensor refs = array;
    refs.block(Arrays.asList(1, 2), Arrays.asList(2, 3)).set(RealScalar.ONE::add, Tensor.ALL, Tensor.ALL);
    assertEquals(array, //
        Tensors.fromString("{{0, 0, 0, 0, 0}, {0, 0, 1, 1, 1}, {0, 0, 1, 1, 1}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}}"));
  }

  @Test
  void testNonRefs() {
    Tensor a = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor b = a.block(Arrays.asList(2), Arrays.asList(3));
    b.set(Array.zeros(3), Tensor.ALL);
    assertEquals(a, Tensors.vector(1, 2, 0, 0, 0, 6));
  }

  @Test
  void testRefs2d() {
    Tensor a = HilbertMatrix.of(5, 6);
    Tensor b = a.block(Arrays.asList(1, 2), Arrays.asList(3, 4));
    b.set(Array.zeros(3, 4), Tensor.ALL, Tensor.ALL);
    Tensor expect = Tensors.fromString( //
        "{{1, 1/2, 1/3, 1/4, 1/5, 1/6}, {1/2, 1/3, 0, 0, 0, 0}, {1/3, 1/4, 0, 0, 0, 0}, {1/4, 1/5, 0, 0, 0, 0}, {1/5, 1/6, 1/7, 1/8, 1/9, 1/10}}");
    assertEquals(a, expect);
  }
}
