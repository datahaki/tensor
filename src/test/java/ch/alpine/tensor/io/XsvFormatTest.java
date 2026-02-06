// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.ext.ReadLine;
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
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    convertCheck( //
        Tensors.matrix((_, _) -> RationalScalar.of(randomGenerator.nextInt(100) - 50, randomGenerator.nextInt(100) + 1), 20, 4));
    convertCheck(Tensors.matrix((_, _) -> DoubleScalar.of(randomGenerator.nextGaussian() * 1e-50), 20, 10));
    convertCheck(Tensors.matrix((_, _) -> DoubleScalar.of(randomGenerator.nextGaussian() * 1e+50), 20, 10));
  }

  @Test
  void testRandom(@TempDir Path tempDir) throws IOException {
    Path path = tempDir.resolve("file.tsv");
    Tensor matrix = RandomVariate.of(DiscreteUniformDistribution.of(-10, 10), 6, 4);
    Export.of(path, matrix);
    Tensor result = Import.of(path);
    assertEquals(matrix, result);
  }

  @ParameterizedTest
  @EnumSource
  void testVector(XsvFormat xsvFormat) {
    Tensor r = Tensors.fromString("{123, 456}");
    List<String> list = xsvFormat.of(r).collect(Collectors.toList());
    Tensor s = xsvFormat.parse(list.stream()); // [[123], [456]]
    assertEquals(Partition.of(r, 1), s);
  }

  @ParameterizedTest
  @EnumSource
  void testScalar(XsvFormat xsvFormat) {
    Tensor r = Scalars.fromString("123");
    List<String> list = xsvFormat.of(r).collect(Collectors.toList());
    Tensor s = xsvFormat.parse(list.stream());
    assertEquals(Tensors.of(Tensors.of(r)), s);
  }

  @Test
  void testImport() throws Exception {
    Path path = Unprotect.path("/ch/alpine/tensor/io/qty/quantity0.csv");
    try (InputStream inputStream = Files.newInputStream(path)) {
      Tensor tensor = XsvFormat.parse( //
          ReadLine.of(inputStream), //
          string -> Tensors.fromString("{" + string + "}"));
      assertEquals(Dimensions.of(tensor), Arrays.asList(2, 2));
      assertInstanceOf(Quantity.class, tensor.Get(0, 0));
      assertInstanceOf(Quantity.class, tensor.Get(0, 1));
      assertInstanceOf(Quantity.class, tensor.Get(1, 0));
      assertInstanceOf(RealScalar.class, tensor.Get(1, 1));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = { "csv", "tsv" })
  void testImport(String ext, @TempDir Path folder) throws Exception {
    Path read = Unprotect.path("/ch/alpine/tensor/io/chinese.csv");
    Tensor tensor = Import.of(read);
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 3));
    assertEquals(tensor.Get(0, 0).toString().length(), 2);
    Path path = folder.resolve("file." + ext);
    Export.of(path, tensor);
    Tensor actual = Import.of(path);
    assertEquals(tensor, actual);
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(XsvFormat.class.getModifiers()));
  }
}
