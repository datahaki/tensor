// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;

class ImageCropTest {
  @Test
  void testSerialization() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = //
        ImageCrop.eq(Tensors.vector(255, 255, 255, 255));
    Serialization.copy(tensorUnaryOperator);
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
  void testVectorFail() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.eq(RealScalar.ONE);
    assertThrows(Throw.class, () -> tensorUnaryOperator.apply(Tensors.vector(1, 2, 3)));
  }
}
