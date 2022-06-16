// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class TensorComparatorTest {
  @Test
  void testSimple() {
    assertEquals(TensorComparator.INSTANCE.compare( //
        Tensors.vector(2, 1), Tensors.vector(2, 1, 3)), Integer.compare(1, 2));
    assertEquals(TensorComparator.INSTANCE.compare( //
        Tensors.vector(2, 1), Tensors.vector(1, 2, 3)), Integer.compare(1, 2));
  }
}
