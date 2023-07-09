// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

class RenameDirectoryTest {
  private static void wrap(File src, File dst) {
    try {
      RenameDirectory.of(src, dst);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  @Test
  void testFailSrcDoesNotExist() {
    assertThrows(RuntimeException.class, () -> wrap(HomeDirectory.file("DOESNOTEXIST"), HomeDirectory.file()));
  }

  @Test
  void testDstAlreadyExist() {
    assertThrows(RuntimeException.class, () -> wrap(HomeDirectory.Documents(), HomeDirectory.Pictures()));
  }

  @Test
  void testSimple(@TempDir File folder) {
    File folder1 = new File(folder, getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File(folder, getClass().getSimpleName() + "3");
    RenameDirectory.of(folder1, folder2);
    assertTrue(folder2.isDirectory());
    folder2.delete();
  }

  @Test
  void testCreateParent(@TempDir File folder) {
    File folder1 = new File(folder, getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File(new File(folder, getClass().getSimpleName() + 2), "sub");
    assertFalse(folder2.exists());
    RenameDirectory.of(folder1, folder2);
    assertTrue(folder2.isDirectory());
    folder2.delete();
    assertTrue(folder2.getParentFile().isDirectory());
    folder2.getParentFile().delete();
  }

  @Test
  @EnabledOnOs(OS.LINUX)
  void testRenameToFail(@TempDir File folder) {
    File folder1 = new File(folder, getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File("/etc", getClass().getSimpleName());
    assertThrows(RuntimeException.class, () -> wrap(folder1, folder2));
    folder1.delete();
  }

  @Test
  @EnabledOnOs(OS.LINUX)
  void testCreateParentFail(@TempDir File folder) {
    File folder1 = new File(folder, getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File("/etc", getClass().getSimpleName() + "/sub");
    assertThrows(RuntimeException.class, () -> wrap(folder1, folder2));
    folder1.delete();
  }
}
