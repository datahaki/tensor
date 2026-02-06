// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class FileBaseNameTest {
  @Test
  void testFile() {
    assertEquals(FileBaseName.of(HomeDirectory.path(".git")), ".git");
    assertEquals(FileBaseName.of(HomeDirectory.path("a.git")), "a");
    assertEquals(FileBaseName.of(HomeDirectory.path("a.git ")), "a");
  }

  @Test
  void testString() {
    assertEquals(FileBaseName.of(Path.of(".git")), ".git");
    assertEquals(FileBaseName.of(Path.of("a.git")), "a");
  }

  @Test
  void testString2() {
    assertEquals(FileBaseName.of(Path.of("/asd/.git")), ".git");
    assertEquals(FileBaseName.of(Path.of("/asd/a.git")), "a");
    assertEquals(FileBaseName.of(Path.of("asd/.git")), ".git");
    assertEquals(FileBaseName.of(Path.of("asd/a.git")), "a");
  }

  @Test
  void testExample() {
    assertEquals(FileBaseName.of(Path.of("/home/user/Documents")), "Documents");
    assertEquals(FileBaseName.of(Path.of("/home/user/info.txt")), "info");
    assertEquals(FileBaseName.of(Path.of("/home/user/info.txt.gz")), "info.txt");
  }
}
