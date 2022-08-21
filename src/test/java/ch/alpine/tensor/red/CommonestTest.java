// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;

class CommonestTest {
  @Test
  void testSimple() {
    Tensor tensor = Commonest.of(Tensors.vector(2, 3, 1, 2, 2, 8, 3, 2));
    assertEquals(tensor, Tensors.vector(2));
  }

  @Test
  void testDupl() {
    Tensor tensor = Commonest.of(Tensors.vector(2, 3, 1, 3, 2));
    assertEquals(Sort.of(tensor), Tensors.vector(2, 3));
  }

  @Test
  void testNonComparable() {
    Tensor tensor = Commonest.of(Tensors.vector(2, 3, 1, 3, 2));
    assertEquals(Sort.of(tensor), Tensors.vector(2, 3));
  }
}
