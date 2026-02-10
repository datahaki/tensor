// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Rotate;
import ch.alpine.tensor.chq.ExactTensorQ;
import test.wrap.SerializableQ;

class LinearSubspaceNullTest {
  @Test
  void testSome() {
    LinearSubspace linearSubspace = LinearSubspace.of(v -> Rotate.PULL.of(v, 1), 3);
    assertEquals(linearSubspace.dimensions(), 0);
    assertEquals(linearSubspace.basis(), Tensors.empty());
    Tensor result = linearSubspace.apply(Tensors.empty());
    assertEquals(result, Tensors.vector(0, 0, 0));
    Tensor projection = linearSubspace.projection(Tensors.vector(2, 3, 4));
    ExactTensorQ.require(projection);
    assertEquals(projection, Tensors.vector(0, 0, 0));
    linearSubspace.toString();
    SerializableQ.require(linearSubspace);
  }
}
