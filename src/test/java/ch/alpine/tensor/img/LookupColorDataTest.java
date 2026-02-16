// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.Vector2Norm;

class LookupColorDataTest {
  @Test
  void testColor() {
    assertEquals(ColorFormat.toColor(LookupColorData.GRAYSCALE.apply(RealScalar.ZERO)), Color.BLACK);
    assertEquals(ColorFormat.toColor(LookupColorData.GRAYSCALE.apply(Rational.HALF)), Color.GRAY);
    assertEquals(ColorFormat.toColor(LookupColorData.GRAYSCALE.apply(RealScalar.ONE)), Color.WHITE);
  }

  @Test
  void testApply() {
    Tensor tensor = ColorDataGradients.GRAYSCALE.apply(RealScalar.of(0.3));
    // assertFalse(ExactTensorQ.of(tensor));
    tensor.set(RealScalar.ONE, 1);
    assertEquals(ColorDataGradients.GRAYSCALE.apply(RealScalar.of(0.3)), //
        Tensors.vector(new Double[] { 77.0, 77.0, 77.0, 255.0 }));
  }

  @Test
  void testTransparent() {
    Tensor vector = LookupColorData.GRAYSCALE.apply(DoubleScalar.POSITIVE_INFINITY);
    assertTrue(Scalars.isZero(Vector2Norm.of(vector)));
    assertEquals(vector, Array.zeros(4));
  }

  @Test
  void testColorData() {
    ColorDataGradient colorDataGradient = ColorDataGradients.GRAYSCALE.deriveWithOpacity(RealScalar.of(0.5));
    assertEquals(colorDataGradient.apply(RealScalar.ZERO), Tensors.vector(0, 0, 0, 128));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(LookupColorData.GRAYSCALE);
  }
}
