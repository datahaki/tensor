// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.usr.AssertFail;

public class ColorDataIndexedTest {
  @Test
  public void testLumaPalette() {
    assertEquals(ColorDataLists._250.cyclic().length(), 13);
    assertEquals(ColorDataLists._250.cyclic().getColor(0), new Color(241, 0, 0, 255));
  }

  @Test
  public void testFailComplex() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._058.cyclic();
    AssertFail.of(() -> colorDataIndexed.apply(ComplexScalar.of(3, 4)));
  }

  @Test
  public void testDeriveFail() {
    AssertFail.of(() -> ColorDataLists._250.cyclic().deriveWithAlpha(256));
    AssertFail.of(() -> ColorDataLists._250.cyclic().deriveWithAlpha(-1));
  }
}
