// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AnimatedGifWriterTest {
  @TempDir
  File tempDir;

  @Test
  void testColor() throws IOException {
    File file = File.createTempFile("file", ".gif", tempDir);
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.isFile());
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 120, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.isFile());
  }

  @Test
  void testColorNonLoop() throws IOException {
    File file = File.createTempFile("file", ".gif", tempDir);
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, false)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.isFile());
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 120, false)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.isFile());
  }

  @Test
  void testGray() throws IOException {
    File file = File.createTempFile("file", ".gif", tempDir);
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
    }
    assertTrue(file.isFile());
  }

  @Test
  void testEmpty() throws IOException {
    File file = File.createTempFile("file", ".gif", tempDir);
    try (AnimatedGifWriter _ = AnimatedGifWriter.of(file, 100, true)) {
      // ---
    }
    assertTrue(file.isFile());
  }
}
