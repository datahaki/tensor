// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AnimatedGifWriterTest {
  @TempDir
  Path tempDir;

  @Test
  void testColor() throws IOException {
    Path file = tempDir.resolve("file123.gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(Files.isRegularFile(file));
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 120, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(Files.isRegularFile(file));
  }

  @Test
  void testColorNonLoop() throws IOException {
    Path file = tempDir.resolve("file234.gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, false)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(Files.isRegularFile(file));
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 120, false)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(Files.isRegularFile(file));
  }

  @Test
  void testGray() throws IOException {
    Path file = tempDir.resolve("file345.gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
    }
    assertTrue(Files.isRegularFile(file));
  }

  @Test
  void testEmpty() throws IOException {
    Path file = tempDir.resolve("file456.gif");
    try (AnimatedGifWriter _ = AnimatedGifWriter.of(file, 100, true)) {
      // ---
    }
    assertTrue(Files.isRegularFile(file));
  }
}
