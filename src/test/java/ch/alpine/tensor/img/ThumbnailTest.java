// code by jph
package ch.alpine.tensor.img;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ThumbnailTest extends TestCase {
  public void testSimple() {
    Tensor tensor = ResourceData.of("/io/image/rgba15x33.png");
    Tensor square = Thumbnail.of(tensor, 7);
    List<Integer> list = Dimensions.of(square);
    assertEquals(list, Arrays.asList(7, 7, 4));
  }

  public void testAuGray() throws IOException {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    Tensor square = Thumbnail.of(tensor, 64);
    List<Integer> list = Dimensions.of(square);
    assertEquals(list, Arrays.asList(64, 64));
    Export.of(HomeDirectory.file("thumb.jpg"), square);
  }

  public void testAuGray1() {
    Tensor tensor = ResourceData.of("/io/image/album_au_gray.jpg");
    AssertFail.of(() -> Thumbnail.of(tensor, -3));
  }
}
