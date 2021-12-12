// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.TestFile;
import junit.framework.TestCase;

public class FileHashTest extends TestCase {
  public void testSimple() throws IOException, NoSuchAlgorithmException {
    File file = TestFile.withExtension("png");
    Export.of(file, Tensors.fromString("{{{0,128,255,255}}}"));
    String string = FileHash.string(file, MessageDigest.getInstance("MD5"));
    assertTrue(file.delete());
    assertEquals(string, "d8c57575d8c6fdf296d4231e8b5f3f22"); // md5sum
  }
}
