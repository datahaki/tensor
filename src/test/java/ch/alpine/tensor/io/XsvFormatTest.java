// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.ReadLine;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;

public class XsvFormatTest {
  private static void convertCheck(Tensor A) {
    for (XsvFormat xsvFormat : XsvFormat.values())
      assertEquals(A, xsvFormat.parse(xsvFormat.of(A)));
  }

  @Test
  public void testCsvR() {
    Random random = new Random();
    convertCheck( //
        Tensors.matrix((i, j) -> RationalScalar.of(random.nextInt(100) - 50, random.nextInt(100) + 1), 20, 4));
    convertCheck(Tensors.matrix((i, j) -> DoubleScalar.of(random.nextGaussian() * 1e-50), 20, 10));
    convertCheck(Tensors.matrix((i, j) -> DoubleScalar.of(random.nextGaussian() * 1e+50), 20, 10));
  }

  @Test
  public void testRandom(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.tsv");
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), 6, 4);
    Export.of(file, matrix);
    Tensor result = Import.of(file);
    assertEquals(matrix, result);
  }

  @Test
  public void testParse() throws IOException {
    try (InputStream inputStream = getClass().getResource("/io/libreoffice_calc.csv").openStream()) {
      try (Stream<String> stream = ReadLine.of(inputStream)) {
        Tensor table = XsvFormat.CSV.parse(stream);
        assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
      }
    }
  }

  @Test
  public void testCount2() throws IOException {
    try (InputStream inputStream = getClass().getResource("/io/libreoffice_calc.csv").openStream()) {
      try (Stream<String> stream = ReadLine.of(inputStream)) {
        Tensor table = XsvFormat.CSV.parse(stream);
        assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
      }
      assertEquals(inputStream.available(), 0);
    }
  }

  @Test
  public void testImport() throws Exception {
    String path = getClass().getResource("/io/qty/quantity0.csv").getPath();
    Tensor tensor = XsvFormat.parse( //
        Files.readAllLines(Paths.get(path)).stream(), //
        string -> Tensors.fromString("{" + string + "}"));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 2));
    assertTrue(tensor.Get(0, 0) instanceof Quantity);
    assertTrue(tensor.Get(0, 1) instanceof Quantity);
    assertTrue(tensor.Get(1, 0) instanceof Quantity);
    assertTrue(tensor.Get(1, 1) instanceof RealScalar);
  }

  @Test
  public void testVisibility() {
    assertFalse(Modifier.isPublic(XsvFormat.class.getModifiers()));
  }
}
