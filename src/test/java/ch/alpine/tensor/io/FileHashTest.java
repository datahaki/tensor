// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.TestFile;

public class FileHashTest {
  @Test
  public void testSimple() throws IOException, NoSuchAlgorithmException {
    File file = TestFile.withExtension("png");
    Export.of(file, Tensors.fromString("{{{0,128,255,255}}}"));
    String string = FileHash.string(file, MessageDigest.getInstance("MD5"));
    assertTrue(file.delete());
    assertEquals(string, "d8c57575d8c6fdf296d4231e8b5f3f22"); // md5sum
  }
}
