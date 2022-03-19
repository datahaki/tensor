// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class DeleteDirectoryTest {
  @Test
  public void testLayer0() throws IOException {
    File folder = HomeDirectory.Downloads(getClass().getSimpleName() + "0");
    folder.mkdir();
    DeleteDirectory deleteDirectory = DeleteDirectory.of(folder, 0, 1, DeleteDirectory.DELETE_FAIL_ABORTS);
    assertEquals(deleteDirectory.fileCount(), 1);
  }

  @Test
  public void testLayer1a() throws IOException {
    File folder = HomeDirectory.Downloads(getClass().getSimpleName() + "1a");
    folder.mkdir();
    File sample1_txt = new File(folder, "sample1.txt");
    sample1_txt.createNewFile();
    assertThrows(Exception.class, () -> DeleteDirectory.of(sample1_txt, 2, 10));
    new File(folder, "sample2.txt").createNewFile();
    assertThrows(Exception.class, () -> DeleteDirectory.of(folder, 0, 5));
    assertThrows(Exception.class, () -> DeleteDirectory.of(folder, 1, 2));
    DeleteDirectory deleteDirectory = DeleteDirectory.of(folder, 1, 3);
    assertEquals(deleteDirectory.fileCount(), 3);
    int reachedDepth = deleteDirectory.reachedDepth();
    assertEquals(reachedDepth, 1);
  }

  @Test
  public void testLayer1b() throws IOException {
    File folder = HomeDirectory.Downloads(getClass().getSimpleName() + "1b");
    folder.mkdir();
    new File(folder, "sample1.txt").createNewFile();
    new File(folder, "sample2.txt").createNewFile();
    File sub = new File(folder, "sub");
    sub.mkdir();
    DeleteDirectory deleteDirectory = DeleteDirectory.of(folder, 1, 5);
    assertEquals(deleteDirectory.fileCount(), 4);
  }

  @Test
  public void testLayer2() throws IOException {
    File folder = HomeDirectory.Downloads(getClass().getSimpleName() + "2");
    folder.mkdir();
    new File(folder, "sample1.txt").createNewFile();
    new File(folder, "sample2.txt").createNewFile();
    File sub = new File(folder, "sub");
    sub.mkdir();
    new File(sub, "content1.txt").createNewFile();
    assertThrows(Exception.class, () -> DeleteDirectory.of(folder, 1, 10));
    DeleteDirectory deleteDirectory = DeleteDirectory.of(folder, 2, 5);
    assertEquals(deleteDirectory.fileCount(), 5);
  }

  @Test
  public void testNotFound() {
    File folder = HomeDirectory.Downloads(getClass().getSimpleName() + "NotFound");
    assertThrows(Exception.class, () -> DeleteDirectory.of(folder, 1, 10));
  }

  @Test
  public void testRenameDirectory() throws IOException {
    File folder1 = HomeDirectory.Downloads(getClass().getSimpleName() + "NotFound1234");
    File folder2 = HomeDirectory.Downloads(getClass().getSimpleName() + "NotFound1235");
    folder1.mkdir();
    {
      File file1 = new File(folder1, "dummy.txt");
      assertTrue(file1.createNewFile());
    }
    boolean renameTo = folder1.renameTo(folder2);
    assertTrue(renameTo);
    folder2.isDirectory();
    File file2 = new File(folder2, "dummy.txt");
    assertTrue(file2.isFile());
    assertTrue(file2.delete());
    assertTrue(folder2.delete());
  }
}
