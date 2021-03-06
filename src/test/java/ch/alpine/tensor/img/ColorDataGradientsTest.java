// code by jph
package ch.alpine.tensor.img;

import java.io.IOException;
import java.util.Arrays;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ColorDataGradientsTest extends TestCase {
  public void testDimensions() {
    for (ColorDataGradient colorDataGradient : ColorDataGradients.values()) {
      assertEquals(Dimensions.of(colorDataGradient.apply(RealScalar.ZERO)), Arrays.asList(4));
      assertEquals(Dimensions.of(colorDataGradient.apply(RealScalar.ONE)), Arrays.asList(4));
    }
  }

  public void testQuantity() {
    Scalar scalar = Quantity.of(Double.POSITIVE_INFINITY, "s");
    for (ColorDataGradient colorDataGradient : ColorDataGradients.values()) {
      Chop.NONE.requireAllZero(colorDataGradient.apply(scalar));
    }
  }

  public void testUnmodified() {
    Scalar nan = DoubleScalar.INDETERMINATE;
    for (ColorDataGradient colorDataGradient : ColorDataGradients.values()) {
      Tensor copy = colorDataGradient.apply(nan);
      colorDataGradient.apply(nan).set(RealScalar.ONE::add, 1);
      assertEquals(copy, colorDataGradient.apply(nan));
      Chop.NONE.requireAllZero(colorDataGradient.apply(nan));
    }
  }

  public void testDeriveWithOpacity() {
    ColorDataGradient colorDataGradient1 = ColorDataGradients.CLASSIC.deriveWithOpacity(RealScalar.ONE);
    ColorDataGradient colorDataGradient2 = ColorDataGradients.CLASSIC.deriveWithOpacity(RationalScalar.HALF);
    Tensor rgba1 = colorDataGradient1.apply(RealScalar.of(0.5));
    Tensor rgba2 = colorDataGradient2.apply(RealScalar.of(0.5));
    assertEquals(rgba1, Tensors.vector(47.5, 195, 33.5, 255));
    assertEquals(rgba1.get(3), RealScalar.of(255));
    assertEquals(rgba2.get(3), RealScalar.of(127.5));
  }

  public void testDeriveWithOpacityAll() throws ClassNotFoundException, IOException {
    for (ColorDataGradient colorDataGradient : ColorDataGradients.values())
      Serialization.copy(colorDataGradient.deriveWithOpacity(RealScalar.of(0.2)));
  }

  public void testStrict() {
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Tensor tensor = ResourceData.of("/colorscheme/" + colorDataGradients.name().toLowerCase() + ".csv");
      LinearColorDataGradient.of(tensor);
    }
  }

  public void testFail() {
    for (ColorDataGradient colorDataGradient : ColorDataGradients.values()) {
      // ColorDataGradients cdg = (ColorDataGradients) colorDataGradient;
      colorDataGradient.apply(RealScalar.of(0.5));
      colorDataGradient.apply(RealScalar.of(0.99));
      if (colorDataGradient.equals(ColorDataGradients.HUE)) {
        // hue is implemented periodically [0, 1) == [1, 2) == ...
      } else {
        AssertFail.of(() -> colorDataGradient.apply(RealScalar.of(-0.1)));
        AssertFail.of(() -> colorDataGradient.apply(RealScalar.of(1.1)));
        AssertFail.of(() -> colorDataGradient.apply(ComplexScalar.of(0.5, 0.5)));
      }
    }
  }
}
