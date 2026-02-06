// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.Tensors;

class FileHashTest {
  @TempDir
  Path tempDir;

  @Test
  void testSimple() throws IOException, NoSuchAlgorithmException {
    Path path = tempDir.resolve("file.png");
    Export.of(path, Tensors.fromString("{{{0,128,255,255}}}"));
    String string = FileHash.string(path, MessageDigest.getInstance("MD5"));
    assertEquals(string, "d8c57575d8c6fdf296d4231e8b5f3f22"); // md5sum
  }
}
