// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HomeDirectoryTest {
  @Test
  void testUserHome() {
    assertTrue(Files.isDirectory(HomeDirectory.path()));
  }

  @Test
  void testNested() {
    Path path = HomeDirectory.path("Doc", "proj1", "admin", "some.txt");
    assertFalse(path.toString().contains(" "));
    assertTrue(path.endsWith("some.txt"));
    assertTrue(path.endsWith(Path.of("admin", "some.txt")));
    assertTrue(path.endsWith(Path.of("proj1", "admin", "some.txt")));
    assertTrue(path.endsWith(Path.of("Doc", "proj1", "admin", "some.txt")));
  }

  @ParameterizedTest
  @EnumSource
  void testDesktop(HomeDirectory homeDirectory) {
    assertTrue(Files.isDirectory(homeDirectory.resolve()));
    assertEquals(homeDirectory.resolve(), HomeDirectory.path(homeDirectory.name()));
    assertEquals(homeDirectory.resolve("test.ico"), HomeDirectory.path(homeDirectory.name(), "test.ico"));
  }

  @Test
  void testFreeSpace() throws IOException {
    Path path = HomeDirectory.path();
    FileStore store = Files.getFileStore(path);
    assertTrue(0 < store.getTotalSpace());
    assertTrue(0 < store.getUsableSpace());
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> HomeDirectory.path("Doc", null, "some.txt"));
  }
}
