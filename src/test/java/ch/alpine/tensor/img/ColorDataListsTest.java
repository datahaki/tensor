// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.qty.Quantity;

class ColorDataListsTest {
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
    assertThrows(TensorRuntimeException.class, () -> colorDataIndexed.apply(Quantity.of(2, "s")));
    assertThrows(TensorRuntimeException.class, () -> colorDataIndexed.apply(Quantity.of(Double.NaN, "s")));
  }

  @ParameterizedTest
  @EnumSource(ColorDataLists.class)
  public void testInfinityTransparentCyclic(ColorDataLists colorDataLists) {
    ColorDataIndexed colorDataIndexed = colorDataLists.cyclic();
    assertEquals(colorDataIndexed.apply(DoubleScalar.INDETERMINATE), Array.zeros(4));
    assertEquals(colorDataIndexed.apply(DoubleScalar.NEGATIVE_INFINITY), Array.zeros(4));
    assertEquals(colorDataIndexed.apply(DoubleScalar.POSITIVE_INFINITY), Array.zeros(4));
  }

  @ParameterizedTest
  @EnumSource(ColorDataLists.class)
  public void testInfinityTransparentStrict(ColorDataLists colorDataLists) {
    ColorDataIndexed colorDataIndexed = colorDataLists.strict();
    assertEquals(colorDataIndexed.apply(DoubleScalar.INDETERMINATE), Array.zeros(4));
    assertEquals(colorDataIndexed.apply(DoubleScalar.NEGATIVE_INFINITY), Array.zeros(4));
    assertEquals(colorDataIndexed.apply(DoubleScalar.POSITIVE_INFINITY), Array.zeros(4));
  }

  @ParameterizedTest
  @ValueSource(ints = { 0, 10, 128, 255 })
  public void testDerive(int alpha) {
    ColorDataIndexed master = ColorDataLists._112.cyclic();
    ColorDataIndexed colorDataIndexed = master.deriveWithAlpha(alpha);
    Color color = colorDataIndexed.getColor(3);
    assertEquals(color.getAlpha(), alpha);
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
    assertTrue(21 <= ColorDataLists.values().length);
  }

  @ParameterizedTest
  @EnumSource(ColorDataLists.class)
  public void testSizeCyclic(ColorDataLists colorDataLists) {
    assertTrue(1 < colorDataLists.cyclic().length());
    assertTrue(colorDataLists.cyclic().length() < 100);
  }

  @ParameterizedTest
  @EnumSource(ColorDataLists.class)
  public void testSizeStrict(ColorDataLists colorDataLists) {
    assertTrue(1 < colorDataLists.strict().length());
    assertTrue(colorDataLists.strict().length() < 100);
  }
}
