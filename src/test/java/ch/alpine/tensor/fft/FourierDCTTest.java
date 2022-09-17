// code by jph
package ch.alpine.tensor.fft;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class FourierDCTTest {
  @Test
  void testSimple() {
    Tensor tensor = FourierDCT.of(Tensors.vector(0, 0, 1, 0, 1));
    Tensor vector = Tensors.vector( //
        0.8944271909999159, -0.4253254041760199, -0.08541019662496846, //
        -0.26286555605956674, 0.5854101966249685);
    Tolerance.CHOP.requireClose(tensor, vector);
  }
}
