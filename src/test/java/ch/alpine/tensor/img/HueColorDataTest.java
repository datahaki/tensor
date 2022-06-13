// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Chop;

class HueColorDataTest {
  @Test
  void testSimple() {
    Tensor color = HueColorData.DEFAULT.apply(RealScalar.of(0.1));
    Tensor alter = ColorDataGradients.HUE.apply(RealScalar.of(0.1));
    Chop._05.requireClose(color, alter);
    assertEquals(HueColorData.DEFAULT.apply(RealScalar.ONE), ColorFormat.toVector(Color.RED));
    assertEquals(HueColorData.DEFAULT.apply(DoubleScalar.POSITIVE_INFINITY), Tensors.vector(0, 0, 0, 0));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(HueColorData.DEFAULT);
  }
}
