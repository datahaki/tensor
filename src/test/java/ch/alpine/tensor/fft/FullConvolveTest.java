// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.HilbertMatrix;

class FullConvolveTest {
  @Test
  public void testSimple() {
    TensorUnaryOperator tuo = FullConvolve.with(Tensors.vector(1, 2, 3));
    Tensor tensor = tuo.apply(HilbertMatrix.of(1, 8));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 8));
    ExactTensorQ.require(tensor);
  }
}
