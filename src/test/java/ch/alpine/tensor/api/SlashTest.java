// code by jph
package ch.alpine.tensor.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.mat.IdentityMatrix;

class SlashTest {
  @Test
  void test() {
    Tensor tensor = Slash.of(Accumulate::of, IdentityMatrix.of(4));
    assertEquals(tensor, Tensors.fromString("{{1, 1, 1, 1}, {0, 1, 1, 1}, {0, 0, 1, 1}, {0, 0, 0, 1}}"));
  }
}
