// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import test.wrap.SerializableQ;

class LinearSubspaceTest {
  @Test
  void testVector() {
    LinearSubspace linearSubspace = LinearSubspace.of(v -> Tensors.vector(1, 0, 0).dot(v), 3);
    assertEquals(linearSubspace.basis(), Tensors.fromString("{{0, 1, 0}, {0, 0, 1}}"));
    Tensor w = linearSubspace.projection(Tensors.vector(3, 4, 5));
    assertEquals(w, Tensors.vector(0, 4, 5));
    assertTrue(linearSubspace.toString().startsWith("LinearSubspace["));
    SerializableQ.require(linearSubspace);
  }
}
