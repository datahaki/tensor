// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

class HomeDirectoryTest {
  @Test
  void testUserHome() {
    assertTrue(HomeDirectory.file().isDirectory());
  }

  @Test
  void testNested() {
    File file = HomeDirectory.file("Doc", "proj1", "some.txt");
    assertFalse(file.toString().contains(" "));
  }

  @Test
  void testDesktop() {
    assertTrue(HomeDirectory.Desktop().isDirectory());
    assertEquals(HomeDirectory.Desktop(), HomeDirectory.file("Desktop"));
    assertEquals(HomeDirectory.Desktop("test.ico"), HomeDirectory.file("Desktop", "test.ico"));
  }

  @Test
  void testDocuments() {
    assertTrue(HomeDirectory.Documents().isDirectory());
    assertEquals(HomeDirectory.Documents(), HomeDirectory.file("Documents"));
    assertEquals(HomeDirectory.Documents("test.txt"), HomeDirectory.file("Documents", "test.txt"));
  }

  @Test
  void testDownloads() {
    assertTrue(HomeDirectory.Downloads().isDirectory());
    assertEquals(HomeDirectory.Downloads(), HomeDirectory.file("Downloads"));
    assertEquals(HomeDirectory.Downloads("test.txt"), HomeDirectory.file("Downloads", "test.txt"));
  }

  @Test
  void testPictures() {
    assertTrue(HomeDirectory.Pictures().isDirectory());
    assertEquals(HomeDirectory.Pictures(), HomeDirectory.file("Pictures"));
    assertEquals(HomeDirectory.Pictures("test.png"), HomeDirectory.file("Pictures", "test.png"));
  }

  @Test
  void testMusic() {
    assertTrue(HomeDirectory.Music().isDirectory());
    assertEquals(HomeDirectory.Music(), HomeDirectory.file("Music"));
    assertEquals(HomeDirectory.Music("test.png"), HomeDirectory.file("Music", "test.png"));
  }

  @Test
  void testVideos() {
    assertTrue(HomeDirectory.Videos().isDirectory());
    assertEquals(HomeDirectory.Videos(), HomeDirectory.file("Videos"));
    assertEquals(HomeDirectory.Videos("test.png"), HomeDirectory.file("Videos", "test.png"));
  }

  @Test
  void testFreeSpace() {
    File file = HomeDirectory.file();
    assertTrue(0 < file.getFreeSpace());
    assertTrue(0 < file.getTotalSpace());
    assertTrue(0 < file.getUsableSpace());
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> HomeDirectory.file("Doc", null, "some.txt"));
  }
}
