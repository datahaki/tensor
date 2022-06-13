// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.ResourceData;

class ThumbnailTest {
  @Test
  void testSimple() {
    Tensor tensor = ResourceData.of("/io/image/rgba15x33.png");
    Tensor square = Thumbnail.of(tensor, 7);
    List<Integer> list = Dimensions.of(square);
    assertEquals(list, Arrays.asList(7, 7, 4));
  }

  @Test
  void testAuGray() {
    Tensor tensor1 = ResourceData.of("/io/image/album_au_gray.jpg");
    Tensor square1 = Thumbnail.of(tensor1, 64);
    List<Integer> list1 = Dimensions.of(square1);
    assertEquals(list1, Arrays.asList(64, 64));
    // Export.of(HomeDirectory.file("thumb.jpg"), square1);
    Tensor tensor2 = Transpose.of(ResourceData.of("/io/image/album_au_gray.jpg"));
    Tensor square2 = Thumbnail.of(tensor2, 64);
    List<Integer> list2 = Dimensions.of(square2);
    assertEquals(list2, Arrays.asList(64, 64));
    assertEquals(Transpose.of(tensor1), tensor2);
  }

  @Test
  void testAuGrayBufferedImage(@TempDir File tempDir) throws IOException {
    BufferedImage original = ResourceData.bufferedImage("/io/image/album_au_gray.jpg");
    BufferedImage expected = Thumbnail.of(original, 64);
    File file = new File(tempDir, "file.jpg");
    assertFalse(file.exists());
    ImageIO.write(expected, "JPG", file);
    assertTrue(file.isFile());
  }

  @Test
  void testAuGray1() {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    assertThrows(IllegalArgumentException.class, () -> Thumbnail.of(tensor, -3));
  }
}
