// code by jph
package ch.alpine.tensor.img;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ImageResizeTest extends TestCase {
  public void testImage1() throws Exception {
    File file = new File(getClass().getResource("/io/image/rgba15x33.png").getFile());
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    Tensor image = ImageResize.nearest(tensor, 2);
    assertEquals(Dimensions.of(image), Arrays.asList(66, 30, 4));
  }

  public void testImage2() throws Exception {
    File file = new File(getClass().getResource("/io/image/rgba15x33.png").getFile());
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    Tensor image = ImageResize.nearest(tensor, 2, 3);
    assertEquals(Dimensions.of(image), Arrays.asList(66, 45, 4));
  }

  public void testImage3() throws IOException {
    File file = new File(getClass().getResource("/io/image/rgba15x33.png").getFile());
    Tensor tensor = Import.of(file);
    Tensor resize = ImageResize.of(tensor, new Dimension(40, 60));
    assertEquals(Dimensions.of(resize), Arrays.asList(60, 40, 4));
  }

  public void testFactor() throws Exception {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    ImageResize.of(tensor, Pi.VALUE);
    ImageResize.of(tensor, Pi.HALF);
    ImageResize.of(tensor, Pi.HALF.reciprocal());
  }

  public void testFactorNegativeFail() throws Exception {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    AssertFail.of(() -> ImageResize.of(tensor, Pi.VALUE.negate()));
  }

  public void testBufferedImage() {
    BufferedImage bufferedImage = ImageResize.of(ResourceData.bufferedImage("/io/image/album_au_gray.jpg"), 12, 3);
    assertEquals(bufferedImage.getWidth(), 12);
    assertEquals(bufferedImage.getHeight(), 3);
    assertEquals(bufferedImage.getType(), BufferedImage.TYPE_BYTE_GRAY);
  }

  public void testImageResizeGray() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 10, 20);
    Tensor resize = ImageResize.of(tensor, new Dimension(50, 20));
    assertEquals(Dimensions.of(resize), Arrays.asList(20, 50));
  }

  public void testBlub1() {
    Tensor tensor = Tensors.fromString("{{0, 1}, {0, 0}}");
    Tensor resize = ImageResize.nearest(tensor, 3);
    assertEquals(resize.get(1), Tensors.vector(0, 0, 0, 1, 1, 1));
    Chop.NONE.requireAllZero(resize.get(Tensor.ALL, 2));
    assertEquals(resize.get(Tensor.ALL, 3), Tensors.vector(1, 1, 1, 0, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 4), Tensors.vector(1, 1, 1, 0, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 5), Tensors.vector(1, 1, 1, 0, 0, 0));
  }

  public void testBlub2() {
    Tensor tensor = Tensors.fromString("{{0, 1}, {0, 0}}"); // dims=[2, 2]
    Tensor resize = ImageResize.nearest(tensor, 2, 3); // dims=[4, 6]
    assertEquals(resize.get(1), Tensors.vector(0, 0, 0, 1, 1, 1));
    assertEquals(resize.get(Tensor.ALL, 1), Tensors.vector(0, 0, 0, 0));
    Chop.NONE.requireAllZero(resize.get(Tensor.ALL, 2));
    assertEquals(resize.get(Tensor.ALL, 3), Tensors.vector(1, 1, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 4), Tensors.vector(1, 1, 0, 0));
    assertEquals(resize.get(Tensor.ALL, 5), Tensors.vector(1, 1, 0, 0));
  }

  public void testRank4() {
    Tensor image = Array.zeros(2, 3, 2, 3);
    Tensor tensor = ImageResize.nearest(image, 2);
    assertEquals(tensor, Array.zeros(4, 6, 2, 3));
    ExactTensorQ.require(tensor);
  }

  public void testFail() {
    Tensor image = Array.zeros(10, 10, 4);
    ImageResize.nearest(image, 2);
    AssertFail.of(() -> ImageResize.nearest(image, 0));
    AssertFail.of(() -> ImageResize.nearest(image, -1));
    AssertFail.of(() -> ImageResize.nearest(image, -1, 2));
    AssertFail.of(() -> ImageResize.nearest(image, 2, -1));
  }

  public void testImageResizeFail() {
    AssertFail.of(() -> ImageResize.of(Pi.TWO, new Dimension(50, 20)));
    AssertFail.of(() -> ImageResize.of(Tensors.vector(1, 2, 3, 4), new Dimension(50, 20)));
  }

  public void testImageResizeNegative1Fail() {
    Tensor tensor = HilbertMatrix.of(3);
    AssertFail.of(() -> ImageResize.of(tensor, new Dimension(50, -20)));
    AssertFail.of(() -> ImageResize.of(tensor, new Dimension(-50, 20)));
  }

  public void testImageResizeNegative2Fail() {
    Tensor tensor = HilbertMatrix.of(3);
    ImageResize.of(tensor, 10, 10);
    AssertFail.of(() -> ImageResize.of(tensor, 50, -20));
    AssertFail.of(() -> ImageResize.of(tensor, -50, 20));
  }
}
