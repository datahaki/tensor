// code by jph
package ch.ethz.idsc.tensor.img;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ImageRotateTest extends TestCase {
  public void testSimple() {
    Tensor tensor = ImageRotate.of(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
    assertEquals(tensor, Tensors.fromString("{{3, 6}, {2, 5}, {1, 4}}"));
    assertEquals(ImageRotate.of(tensor), Tensors.fromString("{{6, 5, 4}, {3, 2, 1}}"));
  }

  public void testCw() {
    Tensor tensor = ImageRotate.cw(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
    assertEquals(tensor, Tensors.fromString("{{4, 1}, {5, 2}, {6, 3}}"));
    assertEquals(ImageRotate.cw(tensor), Tensors.fromString("{{6, 5, 4}, {3, 2, 1}}"));
  }

  public void test180() {
    Tensor tensor = ImageRotate._180(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
    assertEquals(tensor, Tensors.fromString("{{6, 5, 4}, {3, 2, 1}}"));
    assertEquals(ImageRotate._180(tensor), Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
  }

  public void test4Identity() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(-3, 3), 4, 6);
    assertEquals(Nest.of(ImageRotate::of, tensor, 4), tensor);
    assertEquals(Nest.of(ImageRotate::cw, tensor, 4), tensor);
    assertEquals(Nest.of(ImageRotate::_180, tensor, 2), tensor);
  }

  public void testCwCcw() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(-3, 3), 4, 6);
    for (int count = 0; count < 4; ++count) {
      Tensor next = ImageRotate.of(tensor);
      assertEquals(tensor, ImageRotate.cw(next));
      tensor = next;
    }
  }

  public void testRank3() {
    ImageRotate.cw(LeviCivitaTensor.of(3));
  }

  public void testScalarFail() {
    AssertFail.of(() -> ImageRotate.of(Pi.HALF));
    AssertFail.of(() -> ImageRotate.cw(Pi.HALF));
    AssertFail.of(() -> ImageRotate._180(Pi.HALF));
  }

  public void testVectorFail() {
    Tensor vector = Range.of(1, 4);
    VectorQ.requireLength(vector, 3);
    AssertFail.of(() -> ImageRotate.of(vector));
    AssertFail.of(() -> ImageRotate.cw(vector));
    AssertFail.of(() -> ImageRotate._180(vector));
  }

  public void testUnstructuredFail() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3}}");
    AssertFail.of(() -> ImageRotate.of(tensor));
    AssertFail.of(() -> ImageRotate.cw(tensor));
    AssertFail.of(() -> ImageRotate._180(tensor));
  }
}
