// code by jph
package ch.alpine.tensor.nrm;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class MeanSquaredLossLayerTest {
  @Test
  void test() {
    Tensor a = Tensors.fromString("{{1, 2}, {3, 5}, {2, 2}}");
    Tensor b = Tensors.fromString("{{1, 3}, {5, 6}, {4, 2}}");
    Scalar result = MeanSquaredLossLayer.of(a, b);
    // IO.println(result);
    result.copy();
  }
}
