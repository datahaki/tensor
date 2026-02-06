// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class HomeDirectoryTest {
  @Test
  void testUserHome() {
    assertTrue(Files.isDirectory(HomeDirectory.file()));
  }

  @Test
  void testNested() {
    Path file = HomeDirectory.file("Doc", "proj1", "some.txt");
    assertFalse(file.toString().contains(" "));
  }

  @Test
  void testDesktop() {
    assertTrue(Files.isDirectory(HomeDirectory.Desktop()));
    assertEquals(HomeDirectory.Desktop(), HomeDirectory.file("Desktop"));
    assertEquals(HomeDirectory.Desktop("test.ico"), HomeDirectory.file("Desktop", "test.ico"));
  }

  @Test
  void testDocuments() {
    assertTrue(Files.isDirectory(HomeDirectory.Documents()));
    assertEquals(HomeDirectory.Documents(), HomeDirectory.file("Documents"));
    assertEquals(HomeDirectory.Documents("test.txt"), HomeDirectory.file("Documents", "test.txt"));
  }

  @Test
  void testDownloads() {
    assertTrue(Files.isDirectory(HomeDirectory.Downloads()));
    assertEquals(HomeDirectory.Downloads(), HomeDirectory.file("Downloads"));
    assertEquals(HomeDirectory.Downloads("test.txt"), HomeDirectory.file("Downloads", "test.txt"));
  }

  @Test
  void testPictures() {
    assertTrue(Files.isDirectory(HomeDirectory.Pictures()));
    assertEquals(HomeDirectory.Pictures(), HomeDirectory.file("Pictures"));
    assertEquals(HomeDirectory.Pictures("test.png"), HomeDirectory.file("Pictures", "test.png"));
  }

  @Test
  void testMusic() {
    assertTrue(Files.isDirectory(HomeDirectory.Music()));
    assertEquals(HomeDirectory.Music(), HomeDirectory.file("Music"));
    assertEquals(HomeDirectory.Music("test.png"), HomeDirectory.file("Music", "test.png"));
  }

  @Test
  void testVideos() {
    assertTrue(Files.isDirectory(HomeDirectory.Videos()));
    assertEquals(HomeDirectory.Videos(), HomeDirectory.file("Videos"));
    assertEquals(HomeDirectory.Videos("test.png"), HomeDirectory.file("Videos", "test.png"));
  }

  @Test
  void testTemplates() {
    assertTrue(Files.isDirectory(HomeDirectory.Templates()));
    assertEquals(HomeDirectory.Templates(), HomeDirectory.file("Templates"));
    assertEquals(HomeDirectory.Templates("test.png"), HomeDirectory.file("Templates", "test.png"));
  }

  @Test
  void testFreeSpace() throws IOException {
    Path file = HomeDirectory.file();
    FileStore store = Files.getFileStore(file);
    assertTrue(0 < store.getTotalSpace());
    assertTrue(0 < store.getUsableSpace());
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> HomeDirectory.file("Doc", null, "some.txt"));
  }
}
