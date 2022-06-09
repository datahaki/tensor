// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.num.Pi;

class HistogramTransformTest {
  @Test
  public void testSimple() {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    Tensor result = HistogramTransform.of(tensor);
    assertEquals(Dimensions.of(tensor), Dimensions.of(result));
  }

  @Test
  public void testIdentity() {
    Tensor tensor = Tensors.of(Range.of(0, 256));
    Tensor result = HistogramTransform.of(tensor);
    assertEquals(tensor, result);
  }

  @Test
  public void testBlackAndWhite() {
    Tensor tensor = Tensors.of(Tensors.vector(255, 0, 255, 0, 0));
    Tensor result = HistogramTransform.of(tensor);
    assertEquals(tensor, result);
  }

  @Test
  public void testBlackAndWhiteWeak() {
    Tensor tensor1 = Tensors.of(Tensors.vector(255, 0, 255, 0, 0));
    Tensor tensor2 = Tensors.of(Tensors.vector(101, 100, 101, 100, 100));
    Tensor result = HistogramTransform.of(tensor2);
    assertEquals(tensor1, result);
  }

  @Test
  public void testOutOfRankFail() {
    Tensor tensor = Tensors.of(Tensors.vector(0, 256, 0, 3));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> HistogramTransform.of(tensor));
  }

  @Test
  public void testNegativeFail() {
    Tensor tensor = Tensors.of(Tensors.vector(0, -0.1, 3));
    assertThrows(TensorRuntimeException.class, () -> HistogramTransform.of(tensor));
  }

  @Test
  public void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> HistogramTransform.of(Pi.VALUE));
  }

  @Test
  public void testVectorFail() {
    assertThrows(TensorRuntimeException.class, () -> HistogramTransform.of(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testRank3Fail() {
    assertThrows(TensorRuntimeException.class, () -> HistogramTransform.of(Array.zeros(2, 2, 2)));
  }
}
