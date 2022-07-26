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
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;

class ImageCropTest {
  @Test
  void testSerialization() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = //
        ImageCrop.color(Tensors.vector(255, 255, 255, 255));
    Serialization.copy(tensorUnaryOperator);
  }

  @Test
  void testGrayscale() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.color(RealScalar.ZERO);
    Tensor image = Tensors.fromString("{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}");
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{1}}"));
  }

  @Test
  void testColor() throws ClassNotFoundException, IOException {
    Tensor image = Tensors.fromString("{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}");
    image = Raster.of(image, ColorDataGradients.CLASSIC);
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(ImageCrop.color(image.get(0, 0)));
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{{255, 237, 237, 255}}}"));
  }

  @Test
  void testNoCropGrayscale() {
    TensorUnaryOperator imagecrop = ImageCrop.color(RealScalar.of(123));
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/gray15x9.png");
    Tensor result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testNoCropRgba() {
    TensorUnaryOperator imagecrop = ImageCrop.color(Tensors.vector(1, 2, 3, 4));
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/rgba15x33.png");
    Tensor result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  void testVectorFail() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.color(RealScalar.ONE);
    assertThrows(Throw.class, () -> tensorUnaryOperator.apply(Tensors.vector(1, 2, 3)));
  }
}
