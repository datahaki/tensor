// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FileBaseNameTest {
  @Test
  void testFile() {
    assertEquals(FileBaseName.of(HomeDirectory.file(".git")), ".git");
    assertEquals(FileBaseName.of(HomeDirectory.file("a.git")), "a");
    assertEquals(FileBaseName.of(HomeDirectory.file("a.git ")), "a");
  }

  @Test
  void testString() {
    assertEquals(FileBaseName.of(".git"), ".git");
    assertEquals(FileBaseName.of("a.git"), "a");
  }

  @Test
  void testString2() {
    assertEquals(FileBaseName.of("/asd/.git"), ".git");
    assertEquals(FileBaseName.of("/asd/a.git"), "a");
    assertEquals(FileBaseName.of("asd/.git"), ".git");
    assertEquals(FileBaseName.of("asd/a.git"), "a");
  }

  @Test
  void testExample() {
    assertEquals(FileBaseName.of("/home/user/Documents"), "Documents");
    assertEquals(FileBaseName.of("/home/user/info.txt"), "info");
    assertEquals(FileBaseName.of("/home/user/info.txt.gz"), "info.txt");
  }
}
