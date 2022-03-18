// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

public class CyclicColorDataIndexedTest {
  @Test
  public void testCustom() {
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
  public void testDerive() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = CyclicColorDataIndexed.of(tensor);
    colorDataIndexed = colorDataIndexed.deriveWithAlpha(255);
    final Color ref0 = new Color(1, 2, 3, 255);
    assertEquals(colorDataIndexed.getColor(0), ref0);
    final Color ref1 = new Color(5, 6, 7, 255);
    assertEquals(colorDataIndexed.getColor(1), ref1);
  }

  @Test
  public void testFailEmpty() {
    assertThrows(TensorRuntimeException.class, () -> CyclicColorDataIndexed.of(Tensors.empty()));
  }

  @Test
  public void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> CyclicColorDataIndexed.of(RealScalar.ZERO));
  }

  @Test
  public void testFailRGB() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}}");
    assertThrows(TensorRuntimeException.class, () -> CyclicColorDataIndexed.of(tensor));
  }
}
