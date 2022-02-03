// code by jph
package ch.alpine.tensor.fft;

import java.util.Arrays;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class FullConvolveTest extends TestCase {
  public void testSimple() {
    TensorUnaryOperator tuo = FullConvolve.with(Tensors.vector(1, 2, 3));
    Tensor tensor = tuo.apply(HilbertMatrix.of(1, 8));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 8));
    ExactTensorQ.require(tensor);
  }
}
