// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HomeDirectoryTest {
  @Test
  void testNested() {
    Path path = HomeDirectory.Documents.resolve("proj1", "admin", "some.txt");
    assertTrue(path.endsWith("some.txt"));
    assertTrue(path.endsWith(Path.of("admin", "some.txt")));
    assertTrue(path.endsWith(Path.of("proj1", "admin", "some.txt")));
    assertTrue(path.endsWith(Path.of("Documents", "proj1", "admin", "some.txt")));
  }

  @ParameterizedTest
  @EnumSource
  void testDesktop(HomeDirectory homeDirectory) {
    assumeTrue(UserName.is("datahaki"));
    String string = homeDirectory.toString();
    Path path = Path.of(string);
    assertTrue(Files.isDirectory(path));
  }

  @Test
  void testFreeSpace() throws IOException {
    Path path = HomeDirectory.Documents.resolve();
    FileStore store = Files.getFileStore(path);
    assertTrue(0 < store.getTotalSpace());
    assertTrue(0 < store.getUsableSpace());
  }

  @Test
  void testHome() {
    Path base = HomeDirectory.Pictures.resolve();
    assertEquals(base, HomeDirectory.Pictures.resolve());
    assertEquals(base, HomeDirectory.Pictures.resolve(""));
    assertEquals(base, HomeDirectory.Pictures.resolve("", ""));
    assertEquals(base, HomeDirectory.Pictures.resolve("", "", ""));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> HomeDirectory.Documents.resolve("here", null, "some.txt"));
    assertThrows(NullPointerException.class, () -> HomeDirectory.Documents.resolve((String) null));
  }
}
