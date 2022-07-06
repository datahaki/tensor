// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Throw;

class ColorDataIndexedTest {
  @Test
  void testLumaPalette() {
    assertEquals(ColorDataLists._250.cyclic().length(), 13);
    assertEquals(ColorDataLists._250.cyclic().getColor(0), new Color(241, 0, 0, 255));
  }

  @Test
  void testFailComplex() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._058.cyclic();
    assertThrows(Throw.class, () -> colorDataIndexed.apply(ComplexScalar.of(3, 4)));
  }

  @Test
  void testDeriveFail() {
    assertThrows(IllegalArgumentException.class, () -> ColorDataLists._250.cyclic().deriveWithAlpha(256));
    assertThrows(IllegalArgumentException.class, () -> ColorDataLists._250.cyclic().deriveWithAlpha(-1));
  }
}
