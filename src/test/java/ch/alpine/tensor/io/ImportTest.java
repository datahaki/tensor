// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.DataFormatException;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.TestFile;
import junit.framework.TestCase;

public class ImportTest extends TestCase {
  private static final File IO_OBJECT = new File("src/test/resources/io/object");
  public static final File IO_OBJECT_TENSOR = new File(IO_OBJECT, "tensor.object");
  public static final File IO_OBJECT_UNMODIFIABLE = new File(IO_OBJECT, "unmodifiable.object");
  public static final Tensor CONTENT = Tensors.of( //
      RealScalar.ONE, //
      RealScalar.of(3.15), //
      Pi.in(30), //
      ComplexScalar.of(2, 3), //
      Quantity.of(3, "m"), //
      GaussScalar.of(3, 17), //
      Quaternion.of(3, 4, 5, 6));

  public void testCsv() throws Exception {
    String string = "/io/libreoffice_calc.csv";
    File file = new File(getClass().getResource(string).getFile());
    Tensor table = Import.of(file);
    assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
    assertEquals(ResourceData.of(string), table);
  }

  public void testCsvEmpty() throws Exception {
    String string = "/io/empty.csv"; // file has byte length 0
    File file = new File(getClass().getResource(string).getFile());
    assertTrue(Tensors.isEmpty(Import.of(file)));
    assertTrue(Tensors.isEmpty(ResourceData.of(string)));
  }

  public void testCsvEmptyLine() throws Exception {
    String string = "/io/emptyline.csv"; // file consist of a single line break character
    File file = new File(getClass().getResource(string).getFile());
    Tensor expected = Tensors.fromString("{{}}").unmodifiable();
    assertEquals(Import.of(file), expected);
    assertEquals(ResourceData.of(string), expected);
  }

  public void testCsvFail() throws Exception {
    File file = new File("/io/doesnotexist.csv");
    try {
      Import.of(file);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testCsvGz() throws Exception {
    String string = "/io/mathematica23.csv.gz";
    File file = new File(getClass().getResource(string).getFile());
    Tensor table = Import.of(file);
    assertEquals(table, Tensors.fromString("{{123/875+I, 9.3}, {-9, 5/8123123123123123, 1010101}}"));
  }

  /** gjoel noticed that on java9/windows Files::lines in an old implementation of
   * Import::of the file was not closed sufficiently fast to allow the deletion of
   * the file. */
  public void testCsvClosed() throws IOException {
    File file = TestFile.withExtension("csv");
    Export.of(file, Tensors.fromString("{{1, 2}, {3, 4}}"));
    assertTrue(file.isFile());
    assertTrue(8 <= file.length());
    Import.of(file);
    assertTrue(file.delete());
  }

  public void testImageClose() throws Exception {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4}}");
    File file = TestFile.withExtension("png");
    Export.of(file, tensor);
    assertTrue(file.isFile());
    Tensor image = Import.of(file);
    assertEquals(tensor, image);
    assertTrue(file.delete());
  }

  public void testFolderCsvClosed() throws IOException {
    File file = TestFile.withExtension("csv");
    Export.of(file, Tensors.fromString("{{1, 2}, {3, 4}, {5, 6}}"));
    assertTrue(file.isFile());
    assertTrue(12 <= file.length());
    Tensor table = Import.of(file);
    assertEquals(Dimensions.of(table), Arrays.asList(3, 2));
    assertTrue(file.delete());
  }

  public void testPng() throws Exception {
    File file = new File(getClass().getResource("/io/image/rgba15x33.png").getFile());
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
  }

  public void testPngClose() throws Exception {
    Tensor tensor = ResourceData.of("/io/image/rgba15x33.png");
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    File file = TestFile.withExtension("png");
    Export.of(file, tensor);
    assertTrue(file.isFile());
    Import.of(file);
    assertTrue(file.delete());
  }

  public void testJpg() throws Exception {
    File file = new File(getClass().getResource("/io/image/rgb15x33.jpg").getFile());
    Tensor tensor = Import.of(file);
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
    assertEquals(Tensors.vector(180, 46, 47, 255), tensor.get(21, 3)); // verified with gimp
  }

  public void testObject() throws ClassNotFoundException, DataFormatException, IOException {
    // Export.object(UserHome.file("string.object"), "tensorlib.importtest");
    File file = new File(getClass().getResource("/io/string.object").getFile());
    String string = Import.object(file);
    assertEquals(string, "tensorlib.importtest");
  }

  // TODO reactivate
  public void _testSerialization1() throws ClassNotFoundException, IOException, DataFormatException {
    Tensor tensor = Import.object(IO_OBJECT_TENSOR);
    assertEquals(tensor, CONTENT);
  }

  // TODO reactivate
  public void _testSerialization2() throws ClassNotFoundException, IOException, DataFormatException {
    Tensor tensor = Import.object(IO_OBJECT_UNMODIFIABLE);
    assertTrue(Tensors.isUnmodifiable(tensor));
    assertEquals(tensor, CONTENT);
  }
  // public void testSerialization3() throws ClassNotFoundException, IOException, DataFormatException {
  // Tensor tensor = Import.object(IO_OBJECT_VIEWTENSOR);
  // assertEquals(tensor, CONTENT);
  // }

  public void testUnknownFail() {
    File file = new File(getClass().getResource("/io/extension.unknown").getFile());
    try {
      Import.of(file);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testUnknownObjectFail() {
    File file = new File("doesnotexist.fileext");
    try {
      Import.object(file);
      fail();
    } catch (Exception exception) {
      // ---
      assertTrue(exception instanceof IOException);
    }
  }

  public void testTensor() throws Exception {
    File file = TestFile.withExtension("object");
    Export.object(file, Tensors.vector(1, 2, 3, 4));
    Tensor vector = Import.object(file);
    assertEquals(vector, Tensors.vector(1, 2, 3, 4));
    assertTrue(file.delete());
  }

  public void testProperties() throws FileNotFoundException, IOException {
    File file = new File(getClass().getResource("/io/simple.properties").getFile());
    Properties properties = Import.properties(file);
    assertEquals(Scalars.fromString(properties.get("maxTor").toString()), Quantity.of(3, "m*s"));
  }
}
