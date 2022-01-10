// code by jph
package ch.alpine.tensor.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.usr.AssertFail;
import ch.alpine.tensor.usr.TestFile;
import junit.framework.TestCase;

public class ThumbnailTest extends TestCase {
  public void testSimple() {
    Tensor tensor = ResourceData.of("/io/image/rgba15x33.png");
    Tensor square = Thumbnail.of(tensor, 7);
    List<Integer> list = Dimensions.of(square);
    assertEquals(list, Arrays.asList(7, 7, 4));
  }

  public void testAuGray() {
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

  public void testAuGrayBufferedImage() throws IOException {
    BufferedImage original = ResourceData.bufferedImage("/io/image/album_au_gray.jpg");
    BufferedImage expected = Thumbnail.of(original, 64);
    File file = TestFile.withExtension("jpg");
    assertFalse(file.exists());
    ImageIO.write(expected, "JPG", file);
    assertTrue(file.isFile());
    file.delete();
    assertFalse(file.exists());
  }

  public void testAuGray1() {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    AssertFail.of(() -> Thumbnail.of(tensor, -3));
  }
}
