// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.red.Nest;

class ImageRotateTest {
  @Test
  void testSimple() {
    Tensor tensor = ImageRotate.of(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
    assertEquals(tensor, Tensors.fromString("{{3, 6}, {2, 5}, {1, 4}}"));
    assertEquals(ImageRotate.of(tensor), Tensors.fromString("{{6, 5, 4}, {3, 2, 1}}"));
  }

  @Test
  void testCw() {
    Tensor tensor = ImageRotate.cw(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
    assertEquals(tensor, Tensors.fromString("{{4, 1}, {5, 2}, {6, 3}}"));
    assertEquals(ImageRotate.cw(tensor), Tensors.fromString("{{6, 5, 4}, {3, 2, 1}}"));
  }

  @Test
  void test180() {
    Tensor tensor = ImageRotate._180(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
    assertEquals(tensor, Tensors.fromString("{{6, 5, 4}, {3, 2, 1}}"));
    assertEquals(ImageRotate._180(tensor), Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}"));
  }

  @Test
  void test4Identity() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(-3, 3), 4, 6);
    assertEquals(Nest.of(ImageRotate::of, tensor, 4), tensor);
    assertEquals(Nest.of(ImageRotate::cw, tensor, 4), tensor);
    assertEquals(Nest.of(ImageRotate::_180, tensor, 2), tensor);
  }

  @Test
  void testCwCcw() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(-3, 3), 4, 6);
    for (int count = 0; count < 4; ++count) {
      Tensor next = ImageRotate.of(tensor);
      assertEquals(tensor, ImageRotate.cw(next));
      tensor = next;
    }
  }

  @Test
  void testRank3() {
    ImageRotate.cw(LeviCivitaTensor.of(3));
  }

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> ImageRotate.of(Pi.HALF));
    assertThrows(TensorRuntimeException.class, () -> ImageRotate.cw(Pi.HALF));
    assertThrows(TensorRuntimeException.class, () -> ImageRotate._180(Pi.HALF));
  }

  @Test
  void testVectorFail() {
    Tensor vector = Range.of(1, 4);
    VectorQ.requireLength(vector, 3);
    assertThrows(IllegalArgumentException.class, () -> ImageRotate.of(vector));
    assertThrows(IllegalArgumentException.class, () -> ImageRotate.cw(vector));
    assertThrows(IllegalArgumentException.class, () -> ImageRotate._180(vector));
  }

  @Test
  void testUnstructuredFail() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3}}");
    assertThrows(TensorRuntimeException.class, () -> ImageRotate.of(tensor));
    assertThrows(TensorRuntimeException.class, () -> ImageRotate.cw(tensor));
    assertThrows(TensorRuntimeException.class, () -> ImageRotate._180(tensor));
  }
}
