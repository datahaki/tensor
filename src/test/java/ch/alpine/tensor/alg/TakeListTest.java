// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TakeListTest {
  @Test
  void test() {
    Tensor vector = Range.of(0, 8);
    Tensor tensor = TakeList.of(vector, 2, 3, 1, 2);
    assertEquals(tensor, Tensors.fromString("{{0, 1}, {2, 3, 4}, {5}, {6, 7}}"));
  }
}
