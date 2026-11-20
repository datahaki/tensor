// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.mat.IdentityMatrix;

class SlashTest {
  @Test
  void test() {
    Tensor tensor = Slash.of(IdentityMatrix.of(4), Accumulate::of);
    assertEquals(tensor, Tensors.fromString("{{1, 1, 1, 1}, {0, 1, 1, 1}, {0, 0, 1, 1}, {0, 0, 0, 1}}"));
  }
}
