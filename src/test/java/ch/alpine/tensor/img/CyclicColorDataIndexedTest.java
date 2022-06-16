// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class CyclicColorDataIndexedTest {
  @Test
  void testCustom() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = CyclicColorDataIndexed.of(tensor);
    assertEquals(colorDataIndexed.apply(RealScalar.of(1.9 - 20)), tensor.get(1));
    assertEquals(colorDataIndexed.apply(RealScalar.of(1.9)), tensor.get(1));
    assertEquals(colorDataIndexed.apply(RealScalar.of(3.9)), tensor.get(1));
    final Color ref0 = new Color(1, 2, 3, 4);
    assertEquals(colorDataIndexed.getColor(0), ref0);
    assertEquals(colorDataIndexed.getColor(2), ref0);
    assertEquals(colorDataIndexed.getColor(-2), ref0);
    assertEquals(colorDataIndexed.getColor(-12), ref0);
    final Color ref1 = new Color(5, 6, 7, 8);
    assertEquals(colorDataIndexed.getColor(1), ref1);
    assertEquals(colorDataIndexed.getColor(3), ref1);
    assertEquals(colorDataIndexed.getColor(-1), ref1);
    assertEquals(colorDataIndexed.getColor(-11), ref1);
  }

  @Test
  void testDerive() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = CyclicColorDataIndexed.of(tensor);
    colorDataIndexed = colorDataIndexed.deriveWithAlpha(255);
    final Color ref0 = new Color(1, 2, 3, 255);
    assertEquals(colorDataIndexed.getColor(0), ref0);
    final Color ref1 = new Color(5, 6, 7, 255);
    assertEquals(colorDataIndexed.getColor(1), ref1);
  }

  @Test
  void testColors() {
    ColorDataIndexed colorDataIndexed = CyclicColorDataIndexed.of(Color.BLUE, Color.RED, Color.BLACK);
    assertEquals(colorDataIndexed.getColor(-3), Color.BLUE);
    assertEquals(colorDataIndexed.getColor(-2), Color.RED);
    assertEquals(colorDataIndexed.getColor(-1), Color.BLACK);
    assertEquals(colorDataIndexed.getColor(0), Color.BLUE);
    assertEquals(colorDataIndexed.getColor(1), Color.RED);
    assertEquals(colorDataIndexed.getColor(2), Color.BLACK);
  }

  @Test
  void testCornerCase() {
    ColorDataIndexed colorDataIndexed = CyclicColorDataIndexed.of(Color.BLUE, Color.RED);
    assertEquals(ColorFormat.toColor(colorDataIndexed.apply(RealScalar.of(0.2))), Color.BLUE);
    assertEquals(ColorFormat.toColor(colorDataIndexed.apply(RealScalar.of(1.2))), Color.RED);
    assertEquals(ColorFormat.toColor(colorDataIndexed.apply(DoubleScalar.INDETERMINATE)), new Color(0, 0, 0, 0));
    assertEquals(ColorFormat.toColor(colorDataIndexed.apply(DoubleScalar.POSITIVE_INFINITY)), new Color(0, 0, 0, 0));
  }

  @Test
  void testFails() {
    ColorDataIndexed colorDataIndexed = CyclicColorDataIndexed.of(Color.BLUE, Color.RED);
    assertThrows(Exception.class, () -> colorDataIndexed.apply(Quantity.of(1, "m")));
    assertThrows(Exception.class, () -> colorDataIndexed.apply(Quantity.of(Double.NaN, "m")));
    assertThrows(Exception.class, () -> colorDataIndexed.apply(Quantity.of(Double.POSITIVE_INFINITY, "m")));
  }

  @Test
  void testFailEmpty() {
    assertThrows(TensorRuntimeException.class, () -> CyclicColorDataIndexed.of(Tensors.empty()));
  }

  @Test
  void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> CyclicColorDataIndexed.of(RealScalar.ZERO));
  }

  @Test
  void testFailRGB() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}}");
    assertThrows(TensorRuntimeException.class, () -> CyclicColorDataIndexed.of(tensor));
  }
}
