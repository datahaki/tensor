// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.zip.DataFormatException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.qty.Quantity;

class ImportTest {
  private static Tensor load(String string) throws IOException {
    Path file = Unprotect.path(string);
    Tensor tensor = Import.of(file);
    assertEquals(tensor, Import.of(string));
    return tensor;
  }

  @TempDir
  Path tempDir;

  @Test
  void testCsv() throws Exception {
    String string = "/ch/alpine/tensor/io/libreoffice_calc.csv";
    Tensor table = load(string);
    assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
  }

  @Test
  void testCsvEmpty() throws Exception {
    String string = "/ch/alpine/tensor/io/empty.csv"; // file has byte length 0
    Tensor tensor = load(string);
    assertTrue(Tensors.isEmpty(tensor));
  }

  @Test
  void testCsvEmptyLine() throws Exception {
    String string = "/ch/alpine/tensor/io/emptyline.csv"; // file consist of a single line break character
    Tensor tensor = load(string);
    Tensor expected = Tensors.fromString("{{}}").unmodifiable();
    assertEquals(tensor, expected);
  }

  @Test
  void testCsvFail() {
    String string = "/ch/alpine/tensor/io/doesnotexist.csv";
    assertThrows(Exception.class, () -> Unprotect.path(string));
    assertThrows(Exception.class, () -> Import.of(string));
  }

  @Test
  void testCsvGz() throws Exception {
    String string = "/ch/alpine/tensor/io/mathematica23.csv.gz";
    Tensor table = load(string);
    assertEquals(table, Tensors.fromString("{{123/875+I, 9.3}, {-9, 5/8123123123123123, 1010101}}"));
  }

  /** gjoel noticed that on java9/windows Files::lines in an old implementation of
   * Import::of the file was not closed sufficiently fast to allow the deletion of
   * the file. */
  @Test
  void testCsvClosed() throws IOException {
    Path path = tempDir.resolve("file123.csv");
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4}}");
    Export.of(path, tensor);
    assertTrue(Files.isRegularFile(path));
    assertTrue(8 <= Files.size(path));
    Tensor result = Import.of(path);
    assertEquals(tensor, result);
  }

  @Test
  void testImageClose() throws Exception {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4}}");
    Path path = tempDir.resolve("file234.png");
    Export.of(path, tensor);
    assertTrue(Files.isRegularFile(path));
    Tensor image = Import.of(path);
    assertEquals(tensor, image);
  }

  @Test
  void testFolderCsvClosed() throws IOException {
    Path path = tempDir.resolve("file345.csv");
    Export.of(path, Tensors.fromString("{{1, 2}, {3, 4}, {5, 6}}"));
    assertTrue(Files.isRegularFile(path));
    assertTrue(12 <= Files.size(path));
    Tensor table = Import.of(path);
    assertEquals(Dimensions.of(table), Arrays.asList(3, 2));
  }

  @Test
  void testPng() throws Exception {
    String string = "/ch/alpine/tensor/img/rgba15x33.png";
    Tensor tensor = load(string);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
  }

  @Test
  void testPngClose() throws Exception {
    String string = "/ch/alpine/tensor/img/rgba15x33.png";
    Tensor tensor = load(string);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    Path path = tempDir.resolve("file456.png");
    Export.of(path, tensor);
    assertTrue(Files.isRegularFile(path));
    Import.of(path);
  }

  @Test
  void testJpg() throws Exception {
    String string = "/ch/alpine/tensor/img/rgb15x33.jpg";
    Tensor tensor = load(string);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    assertEquals(Tensors.vector(180, 46, 47, 255), tensor.get(21, 3)); // verified with gimp
  }

  private static void _checkColorscheme(Interpolation interpolation) {
    assertThrows(IndexOutOfBoundsException.class, () -> interpolation.get(Tensors.vector(256)));
  }

  @Test
  void testColorschemeClassic() {
    Tensor tensor = Import.of("/ch/alpine/tensor/img/colorscheme/classic.csv");
    assertNotNull(tensor);
    assertEquals(Dimensions.of(tensor), Arrays.asList(256, 4));
    Interpolation interpolation = LinearInterpolation.of(tensor);
    assertEquals(interpolation.get(Tensors.vector(255)), Tensors.vector(255, 237, 237, 255));
    _checkColorscheme(interpolation);
  }

  @Test
  void testHue() {
    Tensor tensor = Import.of("/ch/alpine/tensor/img/colorscheme/_hue.csv");
    assertNotNull(tensor);
    assertEquals(Dimensions.of(tensor), Arrays.asList(7, 4));
    Interpolation interpolation = LinearInterpolation.of(tensor);
    assertEquals(interpolation.get(Tensors.vector(0)), Tensors.vector(255, 0, 0, 255));
    _checkColorscheme(interpolation);
  }

  @Test
  void testObject() throws ClassNotFoundException, DataFormatException, IOException {
    // Export.object(UserHome.file("string.object"), "tensorlib.importtest");
    Path path = Unprotect.path("/ch/alpine/tensor/io/string.object");
    String string = Import.object(path);
    assertEquals(string, "tensorlib.importtest");
  }

  @Test
  void testUnknownFail() {
    String string = "/ch/alpine/tensor/io/extension.unknown";
    Path path = Unprotect.path(string);
    assertThrows(Exception.class, () -> Import.of(path));
  }

  @Test
  void testUnknownObjectFail() {
    Path path = Path.of("doesnotexist.fileext");
    assertThrows(IOException.class, () -> Import.object(path));
  }

  @Disabled
  @Test
  void testTensor() throws Exception {
    Path path = Files.createTempFile("file", ".object");
    Export.object(path, Tensors.vector(1, 2, 3, 4));
    Tensor vector = Import.object(path);
    assertEquals(vector, Tensors.vector(1, 2, 3, 4));
  }

  @Test
  void testProperties() throws IOException {
    String string = "/ch/alpine/tensor/io/simple.properties";
    Path path = Unprotect.path(string);
    Properties properties1 = Import.properties(path);
    assertEquals(Scalars.fromString(properties1.get("maxTor").toString()), Quantity.of(3, "m*s"));
    Properties properties2 = ResourceData.properties(string);
    assertEquals(properties1.size(), properties2.size());
  }

  @Test
  void testPrimes() {
    Tensor primes = Import.of("/ch/alpine/tensor/num/primes.vector");
    List<Integer> dimensions = Dimensions.of(primes);
    assertEquals(dimensions.size(), 1);
    assertTrue(500 < dimensions.get(0));
    assertEquals(primes.Get(5), Scalars.fromString("13"));
  }

  @Test
  void testPrimesLines() {
    Tensor linesp = Tensor.of(ResourceData.lines("/ch/alpine/tensor/num/primes.vector").stream().map(Scalars::fromString));
    Tensor vector = Import.of("/ch/alpine/tensor/num/primes.vector");
    assertEquals(linesp, vector);
  }

  @Test
  void testCsvGz2() {
    Tensor actual = Import.of("/ch/alpine/tensor/io/mathematica23.csv.gz");
    Tensor expected = Tensors.fromString("{{123/875+I, 9.3}, {-9, 5/8123123123123123, 1010101}}");
    assertEquals(expected, actual);
  }

  @Test
  void testJpg2() {
    Tensor image = Import.of("/ch/alpine/tensor/img/rgb15x33.jpg");
    assertEquals(Dimensions.of(image), Arrays.asList(33, 15, 4));
  }

  @Test
  void testBmp() {
    Tensor image = Import.of("/ch/alpine/tensor/img/rgb7x11.bmp");
    assertEquals(Dimensions.of(image), Arrays.asList(11, 7, 4));
    assertEquals(image.get(10, 4), Tensors.vector(0, 7, 95, 255));
  }

  @Test
  void testFailNull() {
    assertThrows(RuntimeException.class, () -> Import.of("/ch/alpine/tensor/number/exists.fail"));
    assertThrows(RuntimeException.class, () -> Import.of("/ch/alpine/tensor/number/exists.fail.bmp"));
  }

  @Test
  void testUnknownExtension() {
    assertThrows(RuntimeException.class, () -> Import.of("/ch/alpine/tensor/io/extension.unknown"));
  }

  @Test
  void testCorruptContent() {
    assertThrows(RuntimeException.class, () -> Import.of("/ch/alpine/tensor/io/corrupt.png"));
  }
}
