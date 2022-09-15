// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.spa.SparseArray;

class ImageCropTest {
  @Test
  void testSerialization() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = //
        ImageCrop.eq(Tensors.vector(255, 255, 255, 255));
    Serialization.copy(tensorUnaryOperator);
  }

  @Test
  void testVector() {
    TensorUnaryOperator tensorUnaryOperator = new ImageCrop(1, RealScalar.ZERO::equals);
    Tensor image = Tensors.vectorInt(0, 1, 0);
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{1}"));
  }

  @Test
  void testGrayscale() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(RealScalar.ZERO);
    Tensor image = Tensors.fromString("{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}");
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{1}}"));
  }

  @Test
  void testGrayscale2() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(RealScalar.ZERO);
    Tensor image = Tensors.fromString("{{0, 0, 0}, {0, 1, 0}, {0, 2, 0}, {0, 0, 0}}");
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{1},{2}}"));
  }

  @Test
  void testColumn() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(RealScalar.ZERO);
    Tensor image = Tensors.fromString("{{0, 1, 0}, {0, 2, 0}, {0, 3, 0}}");
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{1}, {2}, {3}}"));
  }

  @Test
  void testGrayscaleEmpty() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(RealScalar.ZERO);
    Tensor image = Array.zeros(5, 6);
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.empty());
  }

  @Test
  void testColor() throws ClassNotFoundException, IOException {
    Tensor image = Tensors.fromString("{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}");
    image = Raster.of(image, ColorDataGradients.CLASSIC);
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(ImageCrop.eq(image.get(0, 0)));
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{{255, 237, 237, 255}}}"));
  }

  @Test
  void testNoCropGrayscale() {
    TensorUnaryOperator imagecrop = ImageCrop.eq(RealScalar.of(123));
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/gray15x9.png");
    Tensor result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testNoCropRgba() {
    TensorUnaryOperator imagecrop = ImageCrop.eq(Tensors.vector(1, 2, 3, 4));
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/rgba15x33.png");
    Tensor result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testArray0() {
    Tensor tensor = Array.zeros(3, 4, 5, 6);
    ArrayQ.require(tensor);
    ImageCrop imageCrop = new ImageCrop(4, RealScalar.ZERO::equals);
    Tensor result = imageCrop.apply(tensor);
    assertEquals(result, Tensors.empty());
  }

  @Test
  void testArray1() {
    Tensor tensor = Array.zeros(3, 4, 5, 6);
    tensor.set(RealScalar.TWO, 1, 2, 3, 2);
    ArrayQ.require(tensor);
    ImageCrop imageCrop = new ImageCrop(4, RealScalar.ZERO::equals);
    Tensor result = imageCrop.apply(tensor);
    assertEquals(result, Tensors.fromString("{{{{2}}}}"));
  }

  @Test
  void testArray2() {
    Tensor tensor = Array.zeros(3, 4, 5, 6);
    tensor.set(RealScalar.TWO, 0, 0, 0, 0);
    ArrayQ.require(tensor);
    ImageCrop imageCrop = new ImageCrop(4, RealScalar.ZERO::equals);
    Tensor result = imageCrop.apply(tensor);
    assertEquals(result, Tensors.fromString("{{{{2}}}}"));
  }

  @Test
  void testArray3() {
    Tensor tensor = Array.zeros(3, 4, 5, 6);
    tensor.set(RealScalar.TWO, 2, 3, 4, 5);
    ArrayQ.require(tensor);
    ImageCrop imageCrop = new ImageCrop(4, RealScalar.ZERO::equals);
    Tensor result = imageCrop.apply(tensor);
    assertEquals(result, Tensors.fromString("{{{{2}}}}"));
  }

  @Test
  void testSparseArray1() {
    Tensor tensor = Array.sparse(3, 4, 5, 6);
    tensor.set(RealScalar.TWO, 1, 2, 3, 2);
    ArrayQ.require(tensor);
    ImageCrop imageCrop = new ImageCrop(4, RealScalar.ZERO::equals);
    Tensor result = imageCrop.apply(tensor);
    assertTrue(result instanceof SparseArray);
    assertEquals(result, Tensors.fromString("{{{{2}}}}"));
  }

  @Test
  void testVectorFail() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(RealScalar.ONE);
    assertThrows(Exception.class, () -> tensorUnaryOperator.apply(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testZeroFail() {
    assertThrows(Exception.class, () -> new ImageCrop(0, RealScalar.ZERO::equals));
  }
}
