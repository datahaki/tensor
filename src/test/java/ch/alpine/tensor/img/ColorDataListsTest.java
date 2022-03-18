// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.qty.Quantity;

public class ColorDataListsTest {
  @Test
  public void testApply() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
    assertEquals(colorDataIndexed.apply(RealScalar.of(2.3)), Tensors.fromString("{143, 176, 50, 255}"));
  }

  @Test
  public void testGetColor() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
    assertEquals(colorDataIndexed.getColor(2), new Color(143, 176, 50, 255));
  }

  @Test
  public void testQuantityTransparent() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._103.cyclic();
    assertEquals(colorDataIndexed.apply(Quantity.of(2, "s")), Array.zeros(4));
  }

  @Test
  public void testInfinityTransparentCyclic() {
    for (ColorDataLists colorDataLists : ColorDataLists.values()) {
      ColorDataIndexed colorDataIndexed = colorDataLists.cyclic();
      assertEquals(colorDataIndexed.apply(DoubleScalar.INDETERMINATE), Array.zeros(4));
      assertEquals(colorDataIndexed.apply(DoubleScalar.NEGATIVE_INFINITY), Array.zeros(4));
      assertEquals(colorDataIndexed.apply(DoubleScalar.POSITIVE_INFINITY), Array.zeros(4));
    }
  }

  @Test
  public void testInfinityTransparentStrict() {
    for (ColorDataLists colorDataLists : ColorDataLists.values()) {
      ColorDataIndexed colorDataIndexed = colorDataLists.strict();
      assertEquals(colorDataIndexed.apply(DoubleScalar.INDETERMINATE), Array.zeros(4));
      assertEquals(colorDataIndexed.apply(DoubleScalar.NEGATIVE_INFINITY), Array.zeros(4));
      assertEquals(colorDataIndexed.apply(DoubleScalar.POSITIVE_INFINITY), Array.zeros(4));
    }
  }

  @Test
  public void testDerive() {
    ColorDataIndexed master = ColorDataLists._112.cyclic();
    for (int alpha = 0; alpha < 256; ++alpha) {
      ColorDataIndexed colorDataIndexed = master.deriveWithAlpha(alpha);
      Color color = colorDataIndexed.getColor(3);
      assertEquals(color.getAlpha(), alpha);
    }
  }

  @Test
  public void testFailNeg() {
    ColorDataIndexed colorDataIndexed = ColorDataLists._058.cyclic();
    colorDataIndexed.apply(RealScalar.of(-0.3));
  }

  @Test
  public void testSize() {
    ColorDataLists colorDataLists = ColorDataLists._097;
    assertEquals(colorDataLists.cyclic().length(), 16);
  }

  @Test
  public void testSize2() {
    assertTrue(21 <= ColorDataLists.values().length);
    for (ColorDataLists colorDataLists : ColorDataLists.values()) {
      assertTrue(1 < colorDataLists.cyclic().length());
      assertTrue(colorDataLists.cyclic().length() < 100);
    }
  }

  @Test
  public void testSize3() {
    for (ColorDataLists colorDataLists : ColorDataLists.values()) {
      assertTrue(1 < colorDataLists.strict().length());
      assertTrue(colorDataLists.strict().length() < 100);
    }
  }
}
