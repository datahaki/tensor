// code by jph
package ch.ethz.idsc.tensor.img;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.usr.AssertFail;
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

  public void testScalarFail() {
    AssertFail.of(() -> HistogramTransform.of(Pi.VALUE));
  }

  public void testVectorFail() {
    AssertFail.of(() -> HistogramTransform.of(Tensors.vector(1, 2, 3)));
  }
}
