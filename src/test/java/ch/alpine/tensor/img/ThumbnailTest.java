// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.Import;

class ThumbnailTest {
  @TempDir
  Path tempDir;

  @Test
  void testSimple() {
    Tensor tensor = Import.of("/ch/alpine/tensor/img/rgba15x33.png");
    Tensor square = Thumbnail.of(tensor, 7);
    List<Integer> list = Dimensions.of(square);
    assertEquals(list, Arrays.asList(7, 7, 4));
  }

  @Test
  void testSingle() {
    Tensor image = Tensors.fromString("{{1,2,3,4,5,6,7}}");
    BufferedImage bufferedImage = Thumbnail.of(ImageFormat.of(image), 1);
    Tensor from = ImageFormat.from(bufferedImage);
    assertEquals(from, Tensors.fromString("{{4}}"));
  }

  @Test
  void testAuGray() {
    Tensor tensor1 = Import.of("/ch/alpine/tensor/img/album_au_gray.jpg");
    Tensor square1 = Thumbnail.of(tensor1, 64);
    List<Integer> list1 = Dimensions.of(square1);
    assertEquals(list1, Arrays.asList(64, 64));
    Tensor tensor2 = Transpose.of(Import.of("/ch/alpine/tensor/img/album_au_gray.jpg"));
    Tensor square2 = Thumbnail.of(tensor2, 64);
    List<Integer> list2 = Dimensions.of(square2);
    assertEquals(list2, Arrays.asList(64, 64));
    assertEquals(Transpose.of(tensor1), tensor2);
  }

  @Test
  void testAuGrayBufferedImage() throws IOException {
    BufferedImage original = ResourceData.bufferedImage("/ch/alpine/tensor/img/album_au_gray.jpg");
    BufferedImage expected = Thumbnail.of(original, 64);
    Path path = tempDir.resolve("file.jpg");
    assertFalse(Files.exists(path));
    ImageIO.write(expected, "JPG", path.toFile());
    assertTrue(Files.isRegularFile(path));
  }

  @Test
  void testAuGray1() {
    Tensor tensor = Import.of("/ch/alpine/tensor/img/album_au_gray.jpg");
    assertThrows(IllegalArgumentException.class, () -> Thumbnail.of(tensor, -3));
  }
}
