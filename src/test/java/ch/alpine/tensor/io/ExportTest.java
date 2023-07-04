// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
  void testMathematica(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.mathematica");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(file, tensor);
    assertEquals(tensor, Import.of(file));
  }

  @Test
  void testMathematicaGz(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.mathematica.gz");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(file, tensor);
    assertEquals(tensor, Import.of(file));
  }

  @ParameterizedTest
  @ValueSource(strings = { "csv", "csv.gz", "tsv", "tsv.gz" })
  void testCsv(String extension, @TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file." + extension);
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I[V]}, {34.1231`32, 556, 3/456, -323/2}}");
    Export.of(file, tensor);
    assertEquals(tensor, Import.of(file));
  }

  @ParameterizedTest
  @ValueSource(strings = { "csv", "csv.gz", "tsv", "tsv.gz" })
  void testCsvLarge(String extension, @TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file." + extension);
    Distribution distribution = BinomialDistribution.of(10, RealScalar.of(0.3));
    Tensor tensor = RandomVariate.of(distribution, 60, 30);
    Export.of(file, tensor);
    Tensor imported = Import.of(file);
    assertEquals(tensor, imported);
    ExactTensorQ.require(imported);
  }

  @Test
  void testJpgColor(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.jpg");
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11, 4), 2);
    image.set(Array.of(f -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(file, image);
    Tensor diff = image.subtract(Import.of(file));
    Scalar total = Flatten.scalars(diff.map(Abs.FUNCTION)).reduce(Scalar::add).get();
    Scalar pixel = total.divide(RealScalar.of(4 * 77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(6)));
  }

  @ParameterizedTest
  @ValueSource(strings = { "jpg", "jpg.gz", "jpg.gz.gz" })
  void testJpgGray(String extension, @TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file." + extension);
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11), 4);
    Export.of(file, image);
    Tensor diff = image.subtract(Import.of(file));
    Scalar total = Flatten.scalars(diff.map(Abs.FUNCTION)).reduce(Scalar::add).get();
    Scalar pixel = total.divide(RealScalar.of(77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(5)));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz", "bmp.gz.gz", "png", "png.gz" })
  void testExactColor(String extension, @TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file." + extension);
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11, 4);
    image.set(Array.of(f -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz", "bmp.gz.gz", "png", "png.gz" })
  void testExactGray(String extension, @TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file." + extension);
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
  }

  @Test
  void testMatlabM(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.m");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2}}");
    Export.of(file, tensor);
  }

  @Test
  void testFailFile(@TempDir File tempDir) {
    File file = new File(tempDir, "folder/does/not/exist/ethz.m");
    assertFalse(file.exists());
    assertThrows(FileNotFoundException.class, () -> Export.of(file, Tensors.empty()));
  }

  @Test
  void testExportPermissionFail() {
    if (!OperatingSystem.isWindows()) {
      File file = new File("/some.csv");
      assertThrows(FileNotFoundException.class, () -> Export.of(file, Tensors.vector(1, 2, 3)));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = { "gz", "ethz.idsc" })
  void testUnknownExtension(String extension, @TempDir File tempDir) {
    File file = new File(tempDir, "file." + extension);
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    assertThrows(IllegalArgumentException.class, () -> Export.of(file, tensor));
  }

  @ParameterizedTest
  @ValueSource(strings = { "bmp", "bmp.gz" })
  void testExportNull(String extension, @TempDir File tempDir) {
    File file = new File(tempDir, "file." + extension);
    assertThrows(NullPointerException.class, () -> Export.of(file, null));
  }

  @Test
  void testObjectNullFail(@TempDir File tempDir) {
    File file = new File(tempDir, "tensor.file");
    assertFalse(file.exists());
    assertThrows(NullPointerException.class, () -> Export.object(file, null));
  }
}
