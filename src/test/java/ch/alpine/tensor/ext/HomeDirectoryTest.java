// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class HomeDirectoryTest {
  @Test
  public void testUserHome() {
    assertTrue(HomeDirectory.file().isDirectory());
  }

  @Test
  public void testNested() {
    File file = HomeDirectory.file("Doc", "proj1", "some.txt");
    assertFalse(file.toString().contains(" "));
  }

  @Test
  public void testDesktop() {
    assertTrue(HomeDirectory.Desktop().isDirectory());
    assertEquals(HomeDirectory.Desktop(), HomeDirectory.file("Desktop"));
    assertEquals(HomeDirectory.Desktop("test.ico"), HomeDirectory.file("Desktop", "test.ico"));
  }

  @Test
  public void testDocuments() {
    assertTrue(HomeDirectory.Documents().isDirectory());
    assertEquals(HomeDirectory.Documents(), HomeDirectory.file("Documents"));
    assertEquals(HomeDirectory.Documents("test.txt"), HomeDirectory.file("Documents", "test.txt"));
  }

  @Test
  public void testDownloads() {
    assertTrue(HomeDirectory.Downloads().isDirectory());
    assertEquals(HomeDirectory.Downloads(), HomeDirectory.file("Downloads"));
    assertEquals(HomeDirectory.Downloads("test.txt"), HomeDirectory.file("Downloads", "test.txt"));
  }

  @Test
  public void testPictures() {
    assertTrue(HomeDirectory.Pictures().isDirectory());
    assertEquals(HomeDirectory.Pictures(), HomeDirectory.file("Pictures"));
    assertEquals(HomeDirectory.Pictures("test.png"), HomeDirectory.file("Pictures", "test.png"));
  }

  @Test
  public void testFreeSpace() {
    File file = HomeDirectory.file();
    assertTrue(0 < file.getFreeSpace());
    assertTrue(0 < file.getTotalSpace());
    assertTrue(0 < file.getUsableSpace());
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> HomeDirectory.file("Doc", null, "some.txt"));
  }
}
