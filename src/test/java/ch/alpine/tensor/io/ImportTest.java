// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.DataFormatException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.qty.Quantity;

class ImportTest {
  @Test
  void testCsv() throws Exception {
    String string = "/io/libreoffice_calc.csv";
    File file = new File(getClass().getResource(string).getFile());
    Tensor table = Import.of(file);
    assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
    assertEquals(ResourceData.of(string), table);
  }

  @Test
  void testCsvEmpty() throws Exception {
    String string = "/io/empty.csv"; // file has byte length 0
    File file = new File(getClass().getResource(string).getFile());
    assertTrue(Tensors.isEmpty(Import.of(file)));
    assertTrue(Tensors.isEmpty(ResourceData.of(string)));
  }

  @Test
  void testCsvEmptyLine() throws Exception {
    String string = "/io/emptyline.csv"; // file consist of a single line break character
    File file = new File(getClass().getResource(string).getFile());
    Tensor expected = Tensors.fromString("{{}}").unmodifiable();
    assertEquals(Import.of(file), expected);
    assertEquals(ResourceData.of(string), expected);
  }

  @Test
  void testCsvFail() {
    File file = new File("/io/doesnotexist.csv");
    assertThrows(Exception.class, () -> Import.of(file));
  }

  @Test
  void testCsvGz() throws Exception {
    String string = "/io/mathematica23.csv.gz";
    File file = new File(getClass().getResource(string).getFile());
    Tensor table = Import.of(file);
    assertEquals(table, Tensors.fromString("{{123/875+I, 9.3}, {-9, 5/8123123123123123, 1010101}}"));
  }

  /** gjoel noticed that on java9/windows Files::lines in an old implementation of
   * Import::of the file was not closed sufficiently fast to allow the deletion of
   * the file. */
  @Test
  void testCsvClosed(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.csv");
    Export.of(file, Tensors.fromString("{{1, 2}, {3, 4}}"));
    assertTrue(file.isFile());
    assertTrue(8 <= file.length());
    Import.of(file);
  }

  @Test
  void testImageClose(@TempDir File tempDir) throws Exception {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4}}");
    File file = new File(tempDir, "file.png");
    Export.of(file, tensor);
    assertTrue(file.isFile());
    Tensor image = Import.of(file);
    assertEquals(tensor, image);
  }

  @Test
  void testFolderCsvClosed(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.csv");
    Export.of(file, Tensors.fromString("{{1, 2}, {3, 4}, {5, 6}}"));
    assertTrue(file.isFile());
    assertTrue(12 <= file.length());
    Tensor table = Import.of(file);
    assertEquals(Dimensions.of(table), Arrays.asList(3, 2));
  }

  @Test
  void testPng() throws Exception {
    File file = new File(getClass().getResource("/io/image/rgba15x33.png").getFile());
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
  }

  @Test
  void testPngClose(@TempDir File tempDir) throws Exception {
    Tensor tensor = ResourceData.of("/io/image/rgba15x33.png");
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    File file = new File(tempDir, "file.png");
    Export.of(file, tensor);
    assertTrue(file.isFile());
    Import.of(file);
  }

  @Test
  void testJpg() throws Exception {
    File file = new File(getClass().getResource("/io/image/rgb15x33.jpg").getFile());
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    assertEquals(Tensors.vector(180, 46, 47, 255), tensor.get(21, 3)); // verified with gimp
  }

  @Test
  void testObject() throws ClassNotFoundException, DataFormatException, IOException {
    // Export.object(UserHome.file("string.object"), "tensorlib.importtest");
    File file = new File(getClass().getResource("/io/string.object").getFile());
    String string = Import.object(file);
    assertEquals(string, "tensorlib.importtest");
  }

  public void _testSerialization1() throws ClassNotFoundException, IOException, DataFormatException {
    Tensor tensor = Import.object(ImportPublic.IO_OBJECT_TENSOR);
    assertEquals(tensor, ImportPublic.CONTENT);
  }

  public void _testSerialization2() throws ClassNotFoundException, IOException, DataFormatException {
    Tensor tensor = Import.object(ImportPublic.IO_OBJECT_UNMODIFIABLE);
    assertTrue(Tensors.isUnmodifiable(tensor));
    assertEquals(tensor, ImportPublic.CONTENT);
  }

  @Test
  void testUnknownFail() {
    File file = new File(getClass().getResource("/io/extension.unknown").getFile());
    assertThrows(IllegalArgumentException.class, () -> Import.of(file));
  }

  @Test
  void testUnknownObjectFail() {
    File file = new File("doesnotexist.fileext");
    assertThrows(IOException.class, () -> Import.object(file));
  }

  @Test
  void testTensor(@TempDir File tempDir) throws Exception {
    File file = new File(tempDir, "file.object");
    Export.object(file, Tensors.vector(1, 2, 3, 4));
    Tensor vector = Import.object(file);
    assertEquals(vector, Tensors.vector(1, 2, 3, 4));
  }

  @Test
  void testProperties() throws FileNotFoundException, IOException {
    File file = new File(getClass().getResource("/io/simple.properties").getFile());
    Properties properties = Import.properties(file);
    assertEquals(Scalars.fromString(properties.get("maxTor").toString()), Quantity.of(3, "m*s"));
  }
}
