// code by jph
package ch.alpine.tensor.col;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.sca.Chop;

class HueColorDataTest {
  @Test
  void testSimple() {
    Tensor color = HueColorData.DEFAULT.apply(RealScalar.of(0.1));
    ExactTensorQ.require(color);
    assertEquals(color, Tensors.vector(255, 153, 0, 255));
    Tensor alter = ColorDataGradients.HUE.apply(RealScalar.of(0.1));
    Chop._05.requireClose(color, alter);
    assertEquals(HueColorData.DEFAULT.apply(RealScalar.ONE), ColorFormat.toVector(Color.RED));
    assertEquals(HueColorData.DEFAULT.apply(DoubleScalar.POSITIVE_INFINITY), Tensors.vector(0, 0, 0, 0));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(HueColorData.DEFAULT);
  }

  @Test
  void testHue() {
    Tensor matrix = Tensors.fromString("{{0, 0.1}, {1, 2}, {1.2, 0.2}}");
    Tensor image = Raster.of(matrix, HueColorData.DEFAULT);
    assertEquals(Dimensions.of(image), Arrays.asList(3, 2, 4));
  }
}
