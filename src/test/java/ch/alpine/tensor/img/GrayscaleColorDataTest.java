// code by jph
package ch.alpine.tensor.img;

import java.awt.Color;
import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.Vector2Norm;
import junit.framework.TestCase;

public class GrayscaleColorDataTest extends TestCase {
  public void testColor() {
    assertEquals(ColorFormat.toColor(GrayscaleColorData.DEFAULT.apply(RealScalar.ZERO)), Color.BLACK);
    assertEquals(ColorFormat.toColor(GrayscaleColorData.DEFAULT.apply(RationalScalar.HALF)), Color.GRAY);
    assertEquals(ColorFormat.toColor(GrayscaleColorData.DEFAULT.apply(RealScalar.ONE)), Color.WHITE);
  }

  public void testApply() {
    Tensor tensor = ColorDataGradients.GRAYSCALE.apply(RealScalar.of(0.3));
    assertFalse(ExactTensorQ.of(tensor));
    tensor.set(RealScalar.ONE, 1);
    assertEquals(ColorDataGradients.GRAYSCALE.apply(RealScalar.of(0.3)), //
        Tensors.vector(new Double[] { 77.0, 77.0, 77.0, 255.0 }));
  }

  public void testTransparent() {
    Tensor vector = GrayscaleColorData.DEFAULT.apply(DoubleScalar.POSITIVE_INFINITY);
    assertTrue(Scalars.isZero(Vector2Norm.of(vector)));
    assertEquals(vector, Array.zeros(4));
  }

  public void testColorData() {
    ColorDataGradient colorDataGradient = ColorDataGradients.GRAYSCALE.deriveWithOpacity(RealScalar.of(0.5));
    assertEquals(colorDataGradient.apply(RealScalar.ZERO), Tensors.vector(0, 0, 0, 128));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(GrayscaleColorData.DEFAULT);
  }
}
