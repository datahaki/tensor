// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AnimatedGifWriterTest {
  @Test
  public void testColor(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.gif");
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
  public void testColorNonLoop(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.gif");
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
  public void testGray(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
    }
    assertTrue(file.isFile());
  }

  @Test
  public void testEmpty(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      // ---
    }
    assertTrue(file.isFile());
  }
}
