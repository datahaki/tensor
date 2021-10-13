// code by jph
package ch.alpine.tensor.img;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HistogramTransformTest extends TestCase {
  public void testSimple() {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    Tensor result = HistogramTransform.of(tensor);
    assertEquals(Dimensions.of(tensor), Dimensions.of(result));
  }

  public void testIdentity() {
    Tensor tensor = Tensors.of(Range.of(0, 256));
    Tensor result = HistogramTransform.of(tensor);
    assertEquals(tensor, result);
  }

  public void testBlackAndWhite() {
    Tensor tensor = Tensors.of(Tensors.vector(255, 0, 255, 0, 0));
    Tensor result = HistogramTransform.of(tensor);
    assertEquals(tensor, result);
  }

  public void testBlackAndWhiteWeak() {
    Tensor tensor1 = Tensors.of(Tensors.vector(255, 0, 255, 0, 0));
    Tensor tensor2 = Tensors.of(Tensors.vector(101, 100, 101, 100, 100));
    Tensor result = HistogramTransform.of(tensor2);
    assertEquals(tensor1, result);
  }

  public void testOutOfRankFail() {
    Tensor tensor = Tensors.of(Tensors.vector(0, 256, 0, 3));
    AssertFail.of(() -> HistogramTransform.of(tensor));
  }

  public void testNegativeFail() {
    Tensor tensor = Tensors.of(Tensors.vector(0, -0.1, 3));
    AssertFail.of(() -> HistogramTransform.of(tensor));
  }

  public void testScalarFail() {
    AssertFail.of(() -> HistogramTransform.of(Pi.VALUE));
  }

  public void testVectorFail() {
    AssertFail.of(() -> HistogramTransform.of(Tensors.vector(1, 2, 3)));
  }

  public void testRank3Fail() {
    AssertFail.of(() -> HistogramTransform.of(Array.zeros(2, 2, 2)));
  }
}
