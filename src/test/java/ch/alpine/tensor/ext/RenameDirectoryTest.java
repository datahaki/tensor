// code by jph
package ch.alpine.tensor.ext;

import java.io.File;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RenameDirectoryTest extends TestCase {
  private static boolean isLinux = System.getProperty("os.name").equals("Linux");

  private static void wrap(File src, File dst) {
    try {
      RenameDirectory.of(src, dst);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  public void testFailSrcDoesNotExist() {
    AssertFail.of(() -> wrap(HomeDirectory.file("DOESNOTEXIST"), HomeDirectory.file()));
  }

  public void testDstAlreadyExist() {
    AssertFail.of(() -> wrap(HomeDirectory.Documents(), HomeDirectory.Pictures()));
  }

  public void testSimple() throws Exception {
    File folder1 = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = HomeDirectory.Pictures(getClass().getSimpleName());
    RenameDirectory.of(folder1, folder2);
    assertTrue(folder2.isDirectory());
    folder2.delete();
  }

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

  public void testRenameToFail() throws Exception {
    if (!isLinux)
      return;
    File folder1 = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File("/etc", getClass().getSimpleName());
    AssertFail.of(() -> wrap(folder1, folder2));
    folder1.delete();
  }

  public void testCreateParentFail() throws Exception {
    if (!isLinux)
      return;
    File folder1 = HomeDirectory.Documents(getClass().getSimpleName());
    assertFalse(folder1.exists());
    folder1.mkdir();
    File folder2 = new File("/etc", getClass().getSimpleName() + "/sub");
    AssertFail.of(() -> wrap(folder1, folder2));
    folder1.delete();
  }
}
