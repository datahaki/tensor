// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

class FileBaseNameTest {
  final String USER_NAME = System.getProperty("user.name");

  @Test
  void testDoesNotCreate() {
    Path path = HomeDirectory.path("doesnotexist");
    assumeTrue(USER_NAME.equals("datahaki"));
    assertFalse(Files.exists(path));
    assertFalse(Files.isDirectory(path));
    Path sub = HomeDirectory.path("doesnotexist", "sub");
    assertFalse(Files.exists(path));
    assertFalse(Files.isDirectory(path));
    assertThrows(Exception.class, () -> Files.list(sub));
  }

  @Test
  void testFile() {
    assertEquals(FileBaseName.of(HomeDirectory.path(".git")), ".git");
    assertEquals(FileBaseName.of(HomeDirectory.path("a.git")), "a");
  }

  @DisabledOnOs(OS.WINDOWS)
  @Test
  void testWhitespace() {
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
