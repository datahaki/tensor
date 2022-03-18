// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.TestFile;

public class AnimatedGifWriterTest {
  @Test
  public void testColor() throws IOException {
    File file = TestFile.withExtension("gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.isFile());
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 120, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.delete());
  }

  @Test
  public void testColorNonLoop() throws IOException {
    File file = TestFile.withExtension("gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, false)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.isFile());
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 120, false)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    }
    assertTrue(file.delete());
  }

  @Test
  public void testGray() throws IOException {
    File file = TestFile.withExtension("gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
      animatedGifWriter.write(new BufferedImage(2, 3, BufferedImage.TYPE_BYTE_GRAY));
    }
    assertTrue(file.delete());
  }

  @Test
  public void testEmpty() throws IOException {
    File file = TestFile.withExtension("gif");
    try (AnimatedGifWriter animatedGifWriter = AnimatedGifWriter.of(file, 100, true)) {
      // ---
    }
    assertTrue(file.isFile());
    assertTrue(file.delete());
  }
}
