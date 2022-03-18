// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.TensorRuntimeException;

public class ColorDataIndexedTest {
  @Test
  public void testLumaPalette() {
    assertEquals(ColorDataLists._250.cyclic().length(), 13);
    assertEquals(ColorDataLists._250.cyclic().getColor(0), new Color(241, 0, 0, 255));
  }

  @Test
  public void testFailComplex() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._058.cyclic();
    assertThrows(TensorRuntimeException.class, () -> colorDataIndexed.apply(ComplexScalar.of(3, 4)));
  }

  @Test
  public void testDeriveFail() {
    assertThrows(IllegalArgumentException.class, () -> ColorDataLists._250.cyclic().deriveWithAlpha(256));
    assertThrows(IllegalArgumentException.class, () -> ColorDataLists._250.cyclic().deriveWithAlpha(-1));
  }
}
