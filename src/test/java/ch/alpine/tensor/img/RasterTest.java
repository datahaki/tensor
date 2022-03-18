// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;

public class RasterTest {
  @Test
  public void testSmall() {
    Tensor matrix = Tensors.fromString("{{0, 0.1}}");
    Tensor image = Raster.of(matrix, ColorDataGradients.CLASSIC);
    assertEquals(Dimensions.of(image), Arrays.asList(1, 2, 4));
  }

  @Test
  public void testHue() {
    Tensor matrix = Tensors.fromString("{{0, 0.1}, {1, 2}, {1.2, 0.2}}");
    Tensor image = Raster.of(matrix, HueColorData.DEFAULT);
    assertEquals(Dimensions.of(image), Arrays.asList(3, 2, 4));
  }

  @Test
  public void testIndexed() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {3, 1}, {3, 2}}");
    for (ColorDataLists colorDataLists : ColorDataLists.values()) {
      Tensor image = matrix.map(colorDataLists.cyclic());
      assertEquals(Dimensions.of(image), Arrays.asList(3, 2, 4));
    }
  }
}
