// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.nrm.FrobeniusNorm;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Floor;

class JpegTest {
  @TempDir
  File tempDir;

  @ParameterizedTest
  @ValueSource(floats = { 0.1f, 0.9f })
  void testSimple(float quality) throws IOException {
    Tensor image = RandomVariate.of(UniformDistribution.unit(30), 10, 20).map(ColorDataGradients.AURORA).map(Floor.FUNCTION);
    // IO.println(image);
    BufferedImage bufferedImage = ImageFormat.of(image);
    File file = new File(tempDir, "asd" + quality + ".jpg");
    assertFalse(file.isFile());
    Jpeg.put(bufferedImage, file, quality);
    assertTrue(file.isFile());
    Tensor readb = Import.of(file);
    Flatten.of(readb);
  }

  @Test
  void testSimple() throws IOException {
    Tensor image = RandomVariate.of(UniformDistribution.unit(30), 10, 20).map(ColorDataGradients.AURORA).map(Floor.FUNCTION);
    BufferedImage bufferedImage = ImageFormat.of(image);
    Tensor resto = ImageFormat.from(bufferedImage);
    Scalar between = FrobeniusNorm.of(image.subtract(resto));
    Scalars.requireZero(between);
    File file = new File(tempDir, "asd.jpg");
    assertFalse(file.isFile());
    Jpeg.put(bufferedImage, file, 1);
    assertTrue(file.isFile());
    Tensor readb = Import.of(file);
    Tensor diff = image.subtract(readb);
    Flatten.of(diff);
  }
}
