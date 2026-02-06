// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

class FileExtensionTest {
  @Test
  void testFile() {
    assertEquals(FileExtension.of(HomeDirectory.path(".git")), "");
    assertEquals(FileExtension.of(HomeDirectory.path("a.git")), "git");
  }

  @DisabledOnOs(OS.WINDOWS)
  @Test
  void testWhitespace() {
    assertEquals(FileExtension.of(HomeDirectory.path("a.git ")), "git ");
  }

  @Test
  void testString() {
    assertEquals(FileExtension.of(".git"), "");
    assertEquals(FileExtension.of("a.git"), "git");
  }

  @Test
  void testString2() {
    assertEquals(FileExtension.of("/asd/.git"), "");
    assertEquals(FileExtension.of("/asd/a.git"), "git");
    assertEquals(FileExtension.of("asd/.git"), "");
    assertEquals(FileExtension.of("asd/a.git"), "git");
  }
}
