// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class RenameDirectoryTest {
  private static void wrap(File src, File dst) {
    try {
      RenameDirectory.of(src, dst);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  @Test
  public void testFailSrcDoesNotExist() {
    assertThrows(RuntimeException.class, () -> wrap(HomeDirectory.file("DOESNOTEXIST"), HomeDirectory.file()));
  }

  @Test
  public void testDstAlreadyExist() {
    assertThrows(RuntimeException.class, () -> wrap(HomeDirectory.Documents(), HomeDirectory.Pictures()));
  }

  @Test
  public void testSimple() throws Exception {
    File folder1 = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = HomeDirectory.Pictures(getClass().getSimpleName());
    RenameDirectory.of(folder1, folder2);
    assertTrue(folder2.isDirectory());
    folder2.delete();
  }

  @Test
  public void testCreateParent() throws Exception {
    File folder1 = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = HomeDirectory.Pictures(getClass().getSimpleName(), "sub");
    RenameDirectory.of(folder1, folder2);
    assertTrue(folder2.isDirectory());
    folder2.delete();
    assertTrue(folder2.getParentFile().isDirectory());
    folder2.getParentFile().delete();
  }

  @Test
  @EnabledOnOs(OS.LINUX)
  public void testRenameToFail() {
    File folder1 = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File("/etc", getClass().getSimpleName());
    assertThrows(RuntimeException.class, () -> wrap(folder1, folder2));
    folder1.delete();
  }

  @Test
  @EnabledOnOs(OS.LINUX)
  public void testCreateParentFail() {
    File folder1 = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File("/etc", getClass().getSimpleName() + "/sub");
    assertThrows(RuntimeException.class, () -> wrap(folder1, folder2));
    folder1.delete();
  }
}
