// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class ColorDataGradientsTest {
  @ParameterizedTest
  @EnumSource(ColorDataGradients.class)
  void testDimensions(ColorDataGradient colorDataGradient) {
    assertEquals(Dimensions.of(colorDataGradient.apply(RealScalar.ZERO)), Arrays.asList(4));
    assertEquals(Dimensions.of(colorDataGradient.apply(RealScalar.ONE)), Arrays.asList(4));
  }

  @ParameterizedTest
  @EnumSource(ColorDataGradients.class)
  void testQuantity(ColorDataGradient colorDataGradient) {
    Scalar scalar = Quantity.of(Double.POSITIVE_INFINITY, "s");
    assertThrows(Exception.class, () -> colorDataGradient.apply(scalar));
  }

  @ParameterizedTest
  @EnumSource(ColorDataGradients.class)
  void testUnmodified(ColorDataGradient colorDataGradient) {
    Scalar nan = DoubleScalar.INDETERMINATE;
    Tensor copy = colorDataGradient.apply(nan);
    colorDataGradient.apply(nan).set(RealScalar.ONE::add, 1);
    assertEquals(copy, colorDataGradient.apply(nan));
    Chop.NONE.requireAllZero(colorDataGradient.apply(nan));
  }

  @Test
  void testDeriveWithOpacity() {
    ColorDataGradient colorDataGradient1 = ColorDataGradients.CLASSIC.deriveWithOpacity(RealScalar.ONE);
    ColorDataGradient colorDataGradient2 = ColorDataGradients.CLASSIC.deriveWithOpacity(RationalScalar.HALF);
    Tensor rgba1 = colorDataGradient1.apply(RealScalar.of(0.5));
    Tensor rgba2 = colorDataGradient2.apply(RealScalar.of(0.5));
    assertEquals(rgba1, Tensors.vector(47.5, 195, 33.5, 255));
    assertEquals(rgba1.get(3), RealScalar.of(255));
    assertEquals(rgba2.get(3), RealScalar.of(127.5));
  }

  @ParameterizedTest
  @EnumSource(ColorDataGradients.class)
  void testDeriveWithOpacityAll(ColorDataGradient colorDataGradient) throws ClassNotFoundException, IOException {
    Serialization.copy(colorDataGradient.deriveWithOpacity(RealScalar.of(0.2)));
  }

  @Test
  void testStrict() {
    int count = 0;
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Optional<Tensor> optional = colorDataGradients.queryTableRgba();
      if (optional.isPresent()) {
        Tensor tableRgba = optional.get();
        LinearColorDataGradient.of(tableRgba);
        ++count;
      }
    }
    assertTrue(34 <= count);
  }

  @Test
  void testSunset() {
    Tensor t1 = Reverse.of(ColorDataGradients.SUNSET.queryTableRgba().orElseThrow());
    Tensor t2 = ColorDataGradients.SUNSET_REVERSED.queryTableRgba().orElseThrow();
    assertEquals(t1, t2);
  }

  @Test
  void testGrayscaleTable() {
    assertThrows(RuntimeException.class, () -> ColorDataGradients.HUE.queryTableRgba().orElseThrow());
    assertThrows(RuntimeException.class, () -> ColorDataGradients.GRAYSCALE.queryTableRgba().orElseThrow());
  }

  @ParameterizedTest
  @EnumSource(ColorDataGradients.class)
  void testFail(ColorDataGradient colorDataGradient) {
    colorDataGradient.apply(RealScalar.of(0.5));
    colorDataGradient.apply(RealScalar.of(0.99));
    if (colorDataGradient.equals(ColorDataGradients.HUE)) {
      // hue is implemented periodically [0, 1) == [1, 2) == ...
    } else {
      assertThrows(IndexOutOfBoundsException.class, () -> colorDataGradient.apply(RealScalar.of(-0.1)));
      assertThrows(IndexOutOfBoundsException.class, () -> colorDataGradient.apply(RealScalar.of(1.1)));
      assertThrows(Throw.class, () -> colorDataGradient.apply(ComplexScalar.of(0.5, 0.5)));
    }
  }
}
