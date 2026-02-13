// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.img.MeanFilter;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Abs;

class ExportTest {
  @TempDir
  Path tempDir;

  @Test
  void testMathematica() throws IOException {
    Path path = tempDir.resolve("fileMathematica.mathematica");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(path, tensor);
    assertEquals(tensor, Import.of(path));
  }

  @Test
  void testMathematicaGz() throws IOException {
    Path path = tempDir.resolve("fileMathematicaGz.mathematica.gz");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(path, tensor);
    assertEquals(tensor, Import.of(path));
  }

  @ParameterizedTest
  @ValueSource(strings = { "csv", "csv.gz", "gz.csv", "tsv", "tsv.gz" })
  void testCsv(String extension) throws IOException {
    Path path = tempDir.resolve("fileCsvFunc." + extension);
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I[V]}, {34.1231`32, 556, 3/456, -323/2}}");
    Export.of(path, tensor);
    assertEquals(tensor, Import.of(path));
  }

  @ParameterizedTest
  @ValueSource(strings = { "csv", "csv.gz", "tsv", "tsv.gz" })
  void testCsvLarge(String extension) throws IOException {
    Path path = tempDir.resolve("fileCsvLarge." + extension);
    Distribution distribution = BinomialDistribution.of(10, RealScalar.of(0.3));
    Tensor tensor = RandomVariate.of(distribution, 60, 30);
    Export.of(path, tensor);
    Tensor imported = Import.of(path);
    assertEquals(tensor, imported);
    ExactTensorQ.require(imported);
  }

  @Test
  void testJpgColor() throws IOException {
    Path path = tempDir.resolve("fileJpgColor.jpg");
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11, 4), 2);
    image.set(Array.of(_ -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(path, image);
    Tensor diff = image.subtract(Import.of(path));
    Scalar total = Flatten.scalars(diff.maps(Abs.FUNCTION)).reduce(Scalar::add).orElseThrow();
    Scalar pixel = total.divide(RealScalar.of(4 * 77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(6)));
  }

  @ParameterizedTest
  @ValueSource(strings = { "jpg", "jpg.gz", "jpg.gz.gz", "gz.jpg" })
  void testJpgGray(String extension) throws IOException {
    Path path = tempDir.resolve("fileJpgGray." + extension);
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11), 4);
    Export.of(path, image);
    Tensor retry = Import.of(path);
    Tensor diff = image.subtract(retry);
    Scalar total = Flatten.scalars(diff.maps(Abs.FUNCTION)).reduce(Scalar::add).orElseThrow();
    Scalar pixel = total.divide(RealScalar.of(77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(5)));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz", "bmp.gz.gz", "png", "png.gz", "gz.png" })
  void testExactColor(String extension) throws IOException {
    Path path = tempDir.resolve("fileExactColor." + extension);
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11, 4);
    image.set(Array.of(_ -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(path, image);
    assertEquals(image, Import.of(path));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz", "bmp.gz.gz", "png", "png.gz" })
  void testExactGray(String extension) throws IOException {
    Path path = tempDir.resolve("fileExactGray." + extension);
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11);
    Export.of(path, image);
    assertEquals(image, Import.of(path));
  }

  @Test
  void testMatlabM() throws IOException {
    Path path = tempDir.resolve("fileMMatlab.m");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2}}");
    Export.of(path, tensor);
  }

  @Test
  void testFailFile() {
    Path path = tempDir.resolve("folder/does/not/exist/ethz.m");
    assertFalse(Files.isRegularFile(path));
    assertThrows(Exception.class, () -> Export.of(path, Tensors.empty()));
  }

  @ParameterizedTest
  @ValueSource(strings = { "gz", "ethz.idsc" })
  void testUnknownExtension(String extension) {
    Path path = tempDir.resolve("fileUnknownExtension." + extension);
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    assertThrows(IllegalArgumentException.class, () -> Export.of(path, tensor));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz" })
  void testExportNull(String extension) {
    Path path = tempDir.resolve("fileExportNull." + extension);
    assertThrows(NullPointerException.class, () -> Export.of(path, null));
  }

  @Test
  void testObjectNullFail() {
    Path path = tempDir.resolve("tensorObjectNull.file");
    assertFalse(Files.isRegularFile(path));
    assertThrows(NullPointerException.class, () -> Export.object(path, null));
  }

  @Test
  void testGrayAlpha() throws IOException {
    Distribution distribution = DiscreteUniformDistribution.forArray(256);
    Tensor image = RandomVariate.of(distribution, 100, 80, 2);
    int x = 10;
    int y = 20;
    Tensor pixel = image.get(y, x);
    Path path = tempDir.resolve("grayscalealpha.png");
    Export.of(path, image);
    try (InputStream inputStream = Files.newInputStream(path)) {
      BufferedImage bufferedImage = ImageIO.read(inputStream);
      int type = bufferedImage.getType();
      assertEquals(type, BufferedImage.TYPE_CUSTOM);
      WritableRaster writableRaster = bufferedImage.getRaster();
      int numBands = writableRaster.getNumBands();
      assertEquals(numBands, 2);
      int[] read = new int[2];
      writableRaster.getPixel(x, y, read);
      assertEquals(pixel, Tensors.vectorInt(read));
    }
  }
}
