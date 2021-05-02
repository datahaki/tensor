// code by jph
package ch.alpine.tensor.img;

import java.awt.Color;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ColorDataIndexedTest extends TestCase {
  public void testLumaPalette() {
    assertEquals(ColorDataLists._250.cyclic().length(), 13);
    assertEquals(ColorDataLists._250.cyclic().getColor(0), new Color(241, 0, 0, 255));
  }

  public void testFailComplex() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._058.cyclic();
    AssertFail.of(() -> colorDataIndexed.apply(ComplexScalar.of(3, 4)));
  }

  public void testDeriveFail() {
    AssertFail.of(() -> ColorDataLists._250.cyclic().deriveWithAlpha(256));
    AssertFail.of(() -> ColorDataLists._250.cyclic().deriveWithAlpha(-1));
  }
}
