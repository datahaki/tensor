// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;

public class ImageCropTest {
  @Test
  public void testSerialization() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tensorUnaryOperator = //
        ImageCrop.color(Tensors.vector(255, 255, 255, 255));
    Serialization.copy(tensorUnaryOperator);
  }

  @Test
  public void testGrayscale() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.color(RealScalar.ZERO);
    Tensor image = Tensors.fromString("{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}");
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{1}}"));
  }

  @Test
  public void testColor() throws ClassNotFoundException, IOException {
    Tensor image = Tensors.fromString("{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}");
    image = Raster.of(image, ColorDataGradients.CLASSIC);
    TensorUnaryOperator tensorUnaryOperator = Serialization.copy(ImageCrop.color(image.get(0, 0)));
    Tensor result = tensorUnaryOperator.apply(image);
    assertEquals(result, Tensors.fromString("{{{255, 237, 237, 255}}}"));
  }

  @Test
  public void testNoCropGrayscale() {
    TensorUnaryOperator imagecrop = ImageCrop.color(RealScalar.of(123));
    Tensor tensor = ResourceData.of("/io/image/gray15x9.png");
    Tensor result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  public void testNoCropRgba() {
    TensorUnaryOperator imagecrop = ImageCrop.color(Tensors.vector(1, 2, 3, 4));
    Tensor tensor = ResourceData.of("/io/image/rgba15x33.png");
    Tensor result = imagecrop.apply(tensor);
    assertEquals(tensor, result);
  }

  @Test
  public void testVectorFail() {
    TensorUnaryOperator tensorUnaryOperator = ImageCrop.color(RealScalar.ONE);
    assertThrows(TensorRuntimeException.class, () -> tensorUnaryOperator.apply(Tensors.vector(1, 2, 3)));
  }
}
