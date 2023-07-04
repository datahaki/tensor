// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.NavigableMap;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.io.OperatingSystem;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

class ImageResizeTest {
  @Test
  void testImage1() throws Exception {
    
    File file = ResourceData.file("/ch/alpine/tensor/img/rgba15x33.png");
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    Tensor image = ImageResize.nearest(tensor, 2);
    assertEquals(Dimensions.of(image), Arrays.asList(66, 30, 4));
  }

  @Test
  void testImage2() throws Exception {
    File file = ResourceData.file("/ch/alpine/tensor/img/rgba15x33.png");
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    Tensor image = ImageResize.nearest(tensor, 2, 3);
    assertEquals(Dimensions.of(image), Arrays.asList(66, 45, 4));
  }

  @Test
  void testImage3() throws IOException {
    File file = ResourceData.file("/ch/alpine/tensor/img/rgba15x33.png");
    Tensor tensor = Import.of(file);
    Tensor resize = ImageResize.of(tensor, new Dimension(40, 60));
    assertEquals(Dimensions.of(resize), Arrays.asList(60, 40, 4));
  }

  @Test
  void testBufferedImageColorNoResize() {
    BufferedImage original = ResourceData.bufferedImage("/ch/alpine/tensor/img/rgba15x33.png");
    BufferedImage bufferedImage = ImageResize.of(original, original.getWidth(), original.getHeight());
    Tensor t1 = ImageFormat.from(original);
    Tensor t2 = ImageFormat.from(bufferedImage);
    Tensor diff = t1.subtract(t2);
    NavigableMap<Scalar, Long> navigableMap = Tally.sorted(Flatten.scalars(diff));
    Clip clip = Clips.absolute(25);
    clip.requireInside(navigableMap.firstKey());
    clip.requireInside(navigableMap.lastKey());
  }

  @Test
  void testFactor() {
    Tensor tensor = Import.of("/ch/alpine/tensor/img/album_au_gray.jpg");
    Tensor dimens = ImageResize.of(tensor, Pi.VALUE);
    assertEquals(Dimensions.of(dimens), Arrays.asList(314, 418));
    ImageResize.of(tensor, Pi.HALF);
    ImageResize.of(tensor, Pi.HALF.reciprocal());
  }

  @Test
  void testFactorNegativeFail() {
    Tensor tensor = Import.of("/ch/alpine/tensor/img/album_au_gray.jpg");
    assertThrows(IllegalArgumentException.class, () -> ImageResize.of(tensor, Pi.VALUE.negate()));
  }

  @Test
  void testBufferedImage() {
    BufferedImage original = ResourceData.bufferedImage("/ch/alpine/tensor/img/album_au_gray.jpg");
    BufferedImage bufferedImage = ImageResize.of(original, 12, 3);
    assertEquals(bufferedImage.getWidth(), 12);
    assertEquals(bufferedImage.getHeight(), 3);
    assertEquals(bufferedImage.getType(), BufferedImage.TYPE_BYTE_GRAY);
  }

  @Test
  void testBufferedImageGrayscaleNoResize() {
    BufferedImage original = ResourceData.bufferedImage("/ch/alpine/tensor/img/album_au_gray.jpg");
    BufferedImage bufferedImage = ImageResize.of(original, original.getWidth(), original.getHeight());
    Tensor t1 = ImageFormat.from(original);
    Tensor t2 = ImageFormat.from(bufferedImage);
    Tensor diff = t1.subtract(t2);
    Scalar norm = Vector1Norm.of(Flatten.scalars(diff)); // 812636
    Sign.requirePositiveOrZero(norm);
    // System.out.println(norm);
  }

  @Test
  void testImageResizeGray() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 10, 20);
    Tensor resize = ImageResize.of(tensor, new Dimension(50, 20));
    assertEquals(Dimensions.of(resize), Arrays.asList(20, 50));
  }

  @Test
  void testBlub1() {
    Tensor tensor = Tensors.fromString("{{0, 1}, {0, 0}}");
    Tensor resize = ImageResize.nearest(tensor, 3);
    assertEquals(resize.get(1), Tensors.vector(0, 0, 0, 1, 1, 1));
    Chop.NONE.requireAllZero(resize.get(Tensor.ALL, 2));
    assertEquals(resize.get(Tensor.ALL, 3), Tensors.vector(1, 1, 1, 0, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 4), Tensors.vector(1, 1, 1, 0, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 5), Tensors.vector(1, 1, 1, 0, 0, 0));
  }

  @Test
  void testBlub2() {
    Tensor tensor = Tensors.fromString("{{0, 1}, {0, 0}}"); // dims=[2, 2]
    Tensor resize = ImageResize.nearest(tensor, 2, 3); // dims=[4, 6]
    assertEquals(resize.get(1), Tensors.vector(0, 0, 0, 1, 1, 1));
    assertEquals(resize.get(Tensor.ALL, 1), Tensors.vector(0, 0, 0, 0));
    Chop.NONE.requireAllZero(resize.get(Tensor.ALL, 2));
    assertEquals(resize.get(Tensor.ALL, 3), Tensors.vector(1, 1, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 4), Tensors.vector(1, 1, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 5), Tensors.vector(1, 1, 0, 0));
  }

  @Test
  void testRank4() {
    Tensor image = Array.zeros(2, 3, 2, 3);
    Tensor tensor = ImageResize.nearest(image, 2);
    assertEquals(tensor, Array.zeros(4, 6, 2, 3));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testFail() {
    Tensor image = Array.zeros(10, 10, 4);
    ImageResize.nearest(image, 2);
    assertThrows(IllegalArgumentException.class, () -> ImageResize.nearest(image, 0));
    assertThrows(IllegalArgumentException.class, () -> ImageResize.nearest(image, -1));
    assertThrows(IllegalArgumentException.class, () -> ImageResize.nearest(image, -1, 2));
    assertThrows(IllegalArgumentException.class, () -> ImageResize.nearest(image, 2, -1));
  }

  @Test
  void testImageResizeFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> ImageResize.of(Pi.TWO, new Dimension(50, 20)));
    assertThrows(IndexOutOfBoundsException.class, () -> ImageResize.of(Tensors.vector(1, 2, 3, 4), new Dimension(50, 20)));
  }

  @Test
  void testImageResizeNegative1Fail() {
    Tensor tensor = HilbertMatrix.of(3);
    assertThrows(IllegalArgumentException.class, () -> ImageResize.of(tensor, new Dimension(50, -20)));
    assertThrows(IllegalArgumentException.class, () -> ImageResize.of(tensor, new Dimension(-50, 20)));
  }

  @Test
  void testImageResizeNegative2Fail() {
    Tensor tensor = HilbertMatrix.of(3);
    ImageResize.of(tensor, 10, 10);
    assertThrows(IllegalArgumentException.class, () -> ImageResize.of(tensor, 50, -20));
    assertThrows(IllegalArgumentException.class, () -> ImageResize.of(tensor, -50, 20));
  }
}
