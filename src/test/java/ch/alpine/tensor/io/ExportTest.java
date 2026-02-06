// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
  @Test
  void testMathematica(@TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file.mathematica");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(path, tensor);
    assertEquals(tensor, Import.of(path));
  }

  @Test
  void testMathematicaGz(@TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file.mathematica.gz");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(path, tensor);
    assertEquals(tensor, Import.of(path));
  }

  @ParameterizedTest
  @ValueSource(strings = { "csv", "csv.gz", "gz.csv", "tsv", "tsv.gz" })
  void testCsv(String extension, @TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file." + extension);
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I[V]}, {34.1231`32, 556, 3/456, -323/2}}");
    Export.of(path, tensor);
    assertEquals(tensor, Import.of(path));
  }

  @ParameterizedTest
  @ValueSource(strings = { "csv", "csv.gz", "tsv", "tsv.gz" })
  void testCsvLarge(String extension, @TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file." + extension);
    Distribution distribution = BinomialDistribution.of(10, RealScalar.of(0.3));
    Tensor tensor = RandomVariate.of(distribution, 60, 30);
    Export.of(path, tensor);
    Tensor imported = Import.of(path);
    assertEquals(tensor, imported);
    ExactTensorQ.require(imported);
  }

  @Test
  void testJpgColor(@TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file.jpg");
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11, 4), 2);
    image.set(Array.of(_ -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(path, image);
    Tensor diff = image.subtract(Import.of(path));
    Scalar total = Flatten.scalars(diff.map(Abs.FUNCTION)).reduce(Scalar::add).orElseThrow();
    Scalar pixel = total.divide(RealScalar.of(4 * 77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(6)));
  }

  @ParameterizedTest
  @ValueSource(strings = { "jpg", "jpg.gz", "jpg.gz.gz", "gz.jpg" })
  void testJpgGray(String extension, @TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file." + extension);
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11), 4);
    Export.of(path, image);
    Tensor retry = Import.of(path);
    Tensor diff = image.subtract(retry);
    Scalar total = Flatten.scalars(diff.map(Abs.FUNCTION)).reduce(Scalar::add).orElseThrow();
    Scalar pixel = total.divide(RealScalar.of(77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(5)));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz", "bmp.gz.gz", "png", "png.gz", "gz.png" })
  void testExactColor(String extension, @TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file." + extension);
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11, 4);
    image.set(Array.of(_ -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(path, image);
    assertEquals(image, Import.of(path));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz", "bmp.gz.gz", "png", "png.gz" })
  void testExactGray(String extension, @TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file." + extension);
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.forArray(256), 7, 11);
    Export.of(path, image);
    assertEquals(image, Import.of(path));
  }

  @Test
  void testMatlabM(@TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file.m");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2}}");
    Export.of(path, tensor);
  }

  @Test
  void testFailFile(@TempDir Path tempDir) {
    Path path = tempDir.resolve("folder/does/not/exist/ethz.m");
    assertFalse(Files.isRegularFile(path));
    assertThrows(Exception.class, () -> Export.of(path, Tensors.empty()));
  }

  @ParameterizedTest
  @ValueSource(strings = { "gz", "ethz.idsc" })
  void testUnknownExtension(String extension, @TempDir Path tempDir) {
    Path path = tempDir.resolve("file." + extension);
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    assertThrows(IllegalArgumentException.class, () -> Export.of(path, tensor));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz" })
  void testExportNull(String extension, @TempDir Path tempDir) {
    Path path = tempDir.resolve("file." + extension);
    assertThrows(NullPointerException.class, () -> Export.of(path, null));
  }

  @Test
  void testObjectNullFail(@TempDir Path tempDir) {
    Path path = tempDir.resolve("tensor.file");
    assertFalse(Files.isRegularFile(path));
    assertThrows(NullPointerException.class, () -> Export.object(path, null));
  }
}
