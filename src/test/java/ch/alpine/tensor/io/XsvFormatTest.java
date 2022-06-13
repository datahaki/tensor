// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;

class XsvFormatTest {
  private static void convertCheck(Tensor A) {
    for (XsvFormat xsvFormat : XsvFormat.values())
      assertEquals(A, xsvFormat.parse(xsvFormat.of(A)));
  }

  @Test
  void testCsvR() {
    Random random = new Random();
    convertCheck( //
        Tensors.matrix((i, j) -> RationalScalar.of(random.nextInt(100) - 50, random.nextInt(100) + 1), 20, 4));
    convertCheck(Tensors.matrix((i, j) -> DoubleScalar.of(random.nextGaussian() * 1e-50), 20, 10));
    convertCheck(Tensors.matrix((i, j) -> DoubleScalar.of(random.nextGaussian() * 1e+50), 20, 10));
  }

  @Test
  void testRandom(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.tsv");
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), 6, 4);
    Export.of(file, matrix);
    Tensor result = Import.of(file);
    assertEquals(matrix, result);
  }

  @ParameterizedTest
  @EnumSource(XsvFormat.class)
  void testVector(XsvFormat xsvFormat) {
    Tensor r = Tensors.fromString("{123, 456}");
    List<String> list = xsvFormat.of(r).collect(Collectors.toList());
    Tensor s = xsvFormat.parse(list.stream()); // [[123], [456]]
    assertEquals(Partition.of(r, 1), s);
  }

  @ParameterizedTest
  @EnumSource(XsvFormat.class)
  void testScalar(XsvFormat xsvFormat) {
    Tensor r = Scalars.fromString("123");
    List<String> list = xsvFormat.of(r).collect(Collectors.toList());
    Tensor s = xsvFormat.parse(list.stream());
    assertEquals(Tensors.of(Tensors.of(r)), s);
  }

  @Test
  void testImport() throws Exception {
    String path = getClass().getResource("/io/qty/quantity0.csv").getPath();
    Tensor tensor = XsvFormat.parse( //
        Files.readAllLines(Paths.get(path)).stream(), //
        string -> Tensors.fromString("{" + string + "}"));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 2));
    assertInstanceOf(Quantity.class, tensor.Get(0, 0));
    assertInstanceOf(Quantity.class, tensor.Get(0, 1));
    assertInstanceOf(Quantity.class, tensor.Get(1, 0));
    assertInstanceOf(RealScalar.class, tensor.Get(1, 1));
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(XsvFormat.class.getModifiers()));
  }
}
