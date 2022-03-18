// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.img.MeanFilter;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.usr.TestFile;

public class ExportTest {
  @Test
  public void testMathematica() throws IOException {
    File file = TestFile.withExtension("mathematica");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(file, tensor);
    assertEquals(tensor, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testMathematicaGz() throws IOException {
    File file = TestFile.withExtension("mathematica.gz");
    Tensor tensor = Tensors.fromString("{{2[m*s^-3], {3.123+3*I[V], {}}}, {{34.1231`32, 556}, 3/456, -323/2}}");
    assertFalse(StringScalarQ.any(tensor));
    Export.of(file, tensor);
    assertEquals(tensor, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testCsv() throws IOException {
    File file = TestFile.withExtension("csv");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I[V]}, {34.1231`32, 556, 3/456, -323/2}}");
    Export.of(file, tensor);
    assertEquals(tensor, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testCsvGz() throws IOException {
    File file = TestFile.withExtension("csv.gz");
    Tensor tensor = Tensors.fromString("{{0, 2, 3.123+3*I[V]}, {34.1231`32, 556, 3/456, -323/2}}");
    Export.of(file, tensor);
    Tensor imported = Import.of(file);
    assertTrue(file.delete());
    assertEquals(tensor, imported);
  }

  @Test
  public void testCsvGzLarge() throws IOException {
    File file = TestFile.withExtension("csv.gz");
    Distribution distribution = BinomialDistribution.of(10, RealScalar.of(0.3));
    Tensor tensor = RandomVariate.of(distribution, 60, 30);
    Export.of(file, tensor);
    Tensor imported = Import.of(file);
    assertTrue(file.delete());
    assertEquals(tensor, imported);
    ExactTensorQ.require(imported);
  }

  @Test
  public void testPngColor() throws IOException {
    File file = TestFile.withExtension("png");
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11, 4);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testPngGray() throws IOException {
    File file = TestFile.withExtension("png");
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testJpgColor() throws IOException {
    File file = TestFile.withExtension("jpg");
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11, 4), 2);
    image.set(Array.of(f -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(file, image);
    Tensor diff = image.subtract(Import.of(file));
    Scalar total = (Scalar) diff.map(Abs.FUNCTION).flatten(-1).reduce(Tensor::add).get();
    Scalar pixel = total.divide(RealScalar.of(4 * 77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(6)));
    assertTrue(file.delete());
  }

  @Test
  public void testJpgGray() throws IOException {
    File file = TestFile.withExtension("jpg");
    Tensor image = MeanFilter.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11), 4);
    Export.of(file, image);
    Tensor diff = image.subtract(Import.of(file));
    Scalar total = (Scalar) diff.map(Abs.FUNCTION).flatten(-1).reduce(Tensor::add).get();
    Scalar pixel = total.divide(RealScalar.of(77.0));
    assertTrue(Scalars.lessEquals(pixel, RealScalar.of(5)));
    assertTrue(file.delete());
  }

  @Test
  public void testBmpColor() throws IOException {
    File file = TestFile.withExtension("bmp");
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11, 4);
    image.set(Array.of(f -> RealScalar.of(255), 7, 11), Tensor.ALL, Tensor.ALL, 3);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testBmpGray() throws IOException {
    File file = TestFile.withExtension("bmp");
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testBmpGzGray() throws IOException {
    File file = TestFile.withExtension("bmp.gz");
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testBmpGzGzGray() throws IOException {
    File file = TestFile.withExtension("bmp.gz.gz");
    Tensor image = RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 7, 11);
    Export.of(file, image);
    assertEquals(image, Import.of(file));
    assertTrue(file.delete());
  }

  @Test
  public void testMatlabM() throws IOException {
    File file = TestFile.withExtension("m");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2}}");
    Export.of(file, tensor);
    assertTrue(file.delete());
  }

  @Test
  public void testFailFile() {
    File file = new File("folder/does/not/exist/ethz.m");
    assertFalse(file.exists());
    try {
      Export.of(file, Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertFalse(file.exists());
  }

  @Test
  public void testGzOnlyFail() {
    File file = TestFile.withExtension("gz");
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    try {
      Export.of(file, tensor);
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertTrue(file.delete());
  }

  @Test
  public void testFailExtension() {
    File file = new File("ethz.idsc");
    assertFalse(file.exists());
    try {
      Export.of(file, Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertFalse(file.exists());
  }

  @Test
  public void testBmpNull() {
    File file = TestFile.withExtension("bmp");
    try {
      Export.of(file, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertFalse(file.exists());
  }

  @Test
  public void testBmpGzNull() {
    File file = TestFile.withExtension("bmp.gz");
    try {
      Export.of(file, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    assertFalse(file.exists());
  }

  @Test
  public void testObjectNullFail() {
    File file = new File("tensorTestObjectNullFail.file");
    assertFalse(file.exists());
    try {
      Export.object(file, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
