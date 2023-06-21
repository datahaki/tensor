// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.fft.ListConvolve;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Round;

class TransposedImageFormatTest {
  static Tensor _readRGBA() throws IOException {
    File file = new File(TransposedImageFormatTest.class.getResource("/ch/alpine/tensor/img/rgba15x33.png").getFile());
    assertTrue(file.isFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    return TransposedImageFormat.from(bufferedImage);
  }

  @Test
  void testRGBAFile() throws Exception {
    Tensor tensor = _readRGBA();
    assertEquals(tensor.get(12, 19), Tensors.vector(118, 130, 146, 200));
    assertEquals(tensor.get(14, 0), Tensors.vector(254, 0, 0, 255)); // almost red, fe0000
    assertEquals(Dimensions.of(tensor), Arrays.asList(15, 33, 4));
  }

  @Test
  void testGrayFile() throws Exception {
    File file = new File(getClass().getResource("/ch/alpine/tensor/img/gray15x9.png").getFile());
    assertTrue(file.isFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    Tensor tensor = TransposedImageFormat.from(bufferedImage);
    // confirmed with gimp
    assertEquals(tensor.Get(2, 0), RealScalar.of(175));
    assertEquals(tensor.Get(2, 1), RealScalar.of(109));
    assertEquals(tensor.Get(2, 2), RealScalar.of(94));
    assertEquals(Dimensions.of(tensor), Arrays.asList(15, 9));
  }

  @Test
  void testGrayJpg() throws Exception {
    File file = new File(getClass().getResource("/ch/alpine/tensor/img/gray15x9.jpg").getFile());
    assertTrue(file.isFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    Tensor tensor = TransposedImageFormat.from(bufferedImage);
    // confirmed with gimp
    assertEquals(tensor.Get(2, 0), RealScalar.of(84));
    assertEquals(tensor.Get(2, 1), RealScalar.of(66));
    assertEquals(tensor.Get(2, 2), RealScalar.of(39));
    assertEquals(tensor.Get(14, 0), RealScalar.ZERO);
    assertEquals(Dimensions.of(tensor), Arrays.asList(15, 9));
  }

  @Test
  void testGrayBimap1() {
    Tensor scale = Array.of(list -> RealScalar.of(list.get(0)), 256, 20);
    assertEquals(scale, TransposedImageFormat.from(TransposedImageFormat.of(scale)));
  }

  @Test
  void testGrayBimap2() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 256);
    Tensor image = RandomVariate.of(distribution, 20, 30);
    Tensor bimap = TransposedImageFormat.from(TransposedImageFormat.of(image));
    assertEquals(image, bimap);
  }

  @Test
  void testRGBAConvert() throws Exception {
    File file = new File(getClass().getResource("/ch/alpine/tensor/img/rgba15x33.png").getFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    Tensor tensor = TransposedImageFormat.from(bufferedImage);
    assertEquals(tensor, TransposedImageFormat.from(TransposedImageFormat.of(tensor)));
  }

  @Test
  void testRGBASmooth() throws Exception {
    File file = new File(getClass().getResource("/ch/alpine/tensor/img/rgba15x33.png").getFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    Tensor tensor = TransposedImageFormat.from(bufferedImage);
    Tensor kernel = Array.of(l -> RationalScalar.of(1, 6), 3, 2, 1);
    Tensor array = ListConvolve.of(kernel, tensor);
    TransposedImageFormat.of(array); // succeeds
  }

  @Test
  void testRGBAInvalid() throws Exception {
    File file = new File(getClass().getResource("/ch/alpine/tensor/img/rgba15x33.png").getFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    Tensor tensor = TransposedImageFormat.from(bufferedImage);
    Tensor kernel = Array.of(l -> RationalScalar.of(1, 1), 3, 5, 1);
    Tensor array = ListConvolve.of(kernel, tensor);
    assertThrows(IllegalArgumentException.class, () -> TransposedImageFormat.of(array));
  }

  private static Tensor _gradients() {
    Tensor arr = Array.of(list -> RealScalar.of(list.get(1)), 3, 11);
    Tensor image = Tensors.empty();
    for (ScalarTensorFunction cdf : ColorDataGradients.values())
      image.append(Raster.of(arr, cdf));
    image = Flatten.of(image, 1);
    image = Transpose.of(image, 1, 0, 2);
    return image.map(Round.FUNCTION);
  }

  @Test
  void testColorBimap() {
    Tensor scale = _gradients();
    assertEquals(scale, TransposedImageFormat.from(TransposedImageFormat.of(scale)));
  }
}
