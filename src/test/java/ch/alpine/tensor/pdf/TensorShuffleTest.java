// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TensorShuffleTest {
  @Test
  void testPermutations() {
    Tensor vector = Tensors.vector(1, 2, 3).unmodifiable();
    Set<Tensor> set = new HashSet<>();
    for (int index = 0; index < 100; ++index)
      set.add(TensorShuffle.of(vector));
    assertTrue(3 < set.size());
  }
}
