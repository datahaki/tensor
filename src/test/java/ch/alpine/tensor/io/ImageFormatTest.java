// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class ImageFormatTest {
  @Test
  void testRGBAFile() throws Exception {
    Tensor tensor = TransposedImageFormatTest._readRGBA();
    String string = "/ch/alpine/tensor/img/rgba15x33.png";
    File file = new File(getClass().getResource(string).getFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    Tensor image = ImageFormat.from(bufferedImage);
    assertEquals(image, Import.of(string));
    assertEquals(Transpose.of(tensor), image);
    // confirmed with gimp
    assertEquals(image.get(32, 0), Tensors.vector(126, 120, 94, 255));
  }

  @Test
  void testGray() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 256);
    Tensor image = RandomVariate.of(distribution, 100, 200);
    Tensor bimap = ImageFormat.from(ImageFormat.of(image));
    assertEquals(image, bimap);
  }

  @Test
  void testColor() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 256);
    Tensor image = RandomVariate.of(distribution, 100, 200, 4);
    Tensor bimap = ImageFormat.from(ImageFormat.of(image));
    assertEquals(image, bimap);
  }

  @Test
  void testGrayFile() throws Exception {
    String string = "/ch/alpine/tensor/img/gray15x9.png";
    File file = new File(getClass().getResource(string).getFile());
    BufferedImage bufferedImage = ImageIO.read(file);
    Tensor tensor = ImageFormat.from(bufferedImage);
    assertEquals(tensor, Import.of(string));
    // confirmed with gimp
    assertEquals(tensor.Get(0, 2), RealScalar.of(175));
    assertEquals(tensor.Get(1, 2), RealScalar.of(109));
    assertEquals(tensor.Get(2, 2), RealScalar.of(94));
    assertEquals(Dimensions.of(tensor), Arrays.asList(9, 15));
  }

  @Test
  void testGrayscale() {
    Tensor tensor = Tensors.of(Range.of(0, 256));
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBuffer dataBuffer = writableRaster.getDataBuffer();
    DataBufferByte dataBufferByte = (DataBufferByte) dataBuffer;
    for (int index = 0; index < 256; ++index)
      assertEquals(dataBufferByte.getData()[index], (byte) index);
    int[] pixel = new int[1];
    for (int index = 0; index < 256; ++index) {
      bufferedImage.getRaster().getPixel(index, 0, pixel);
      assertEquals(index, pixel[0]);
    }
  }
}
