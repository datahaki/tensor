// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.usr.AssertFail;

public class ColorDataGradientTest {
  @Test
  public void testDimensions() {
    Tensor tensor = ResourceData.of("/colorscheme/_hue.csv");
    assertEquals(Dimensions.of(tensor).get(1), Integer.valueOf(4));
    LinearColorDataGradient.of(tensor);
  }

  @Test
  public void testSerializable() throws Exception {
    Tensor tensor = ResourceData.of("/colorscheme/_hue.csv");
    ColorDataGradient cdg = LinearColorDataGradient.of(tensor);
    Serialization.copy(cdg);
  }

  @Test
  public void testModifiable() {
    Tensor tensor = ResourceData.of("/colorscheme/_hue.csv");
    ColorDataGradient cdg = LinearColorDataGradient.of(tensor);
    cdg.apply(RealScalar.ONE).set(RealScalar.ONE, 1);
    cdg.apply(RealScalar.ZERO).set(RealScalar.ONE, 1);
    cdg.apply(DoubleScalar.INDETERMINATE).set(RealScalar.ONE, 1);
  }

  @Test
  public void testCustom() {
    ScalarTensorFunction template = ColorDataGradients.CLASSIC;
    ScalarTensorFunction custom = s -> {
      Tensor rgba = template.apply(s);
      rgba.set(RealScalar.of(128), 3);
      return rgba;
    };
    Tensor rgba = custom.apply(RealScalar.of(0.1));
    assertEquals(rgba.length(), 4);
    assertEquals(rgba.get(3), RealScalar.of(128));
  }

  @Test
  public void testGaussScalar() {
    Scalar scalar = GaussScalar.of(123, 251);
    AssertFail.of(() -> ColorDataGradients.ALPINE.apply(scalar));
  }
}