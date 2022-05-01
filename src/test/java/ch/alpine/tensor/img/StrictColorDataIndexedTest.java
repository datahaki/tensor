// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;

class StrictColorDataIndexedTest {
  @Test
  public void testColors2() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(tensor);
    assertEquals(colorDataIndexed.apply(RealScalar.of(1.9)), tensor.get(1));
    assertEquals(colorDataIndexed.apply(RealScalar.of(1.1)), tensor.get(1));
    assertEquals(colorDataIndexed.apply(RealScalar.of(0.9)), tensor.get(0));
    final Color ref0 = new Color(1, 2, 3, 4);
    assertEquals(colorDataIndexed.getColor(0), ref0);
    final Color ref1 = new Color(5, 6, 7, 8);
    assertEquals(colorDataIndexed.getColor(1), ref1);
  }

  @Test
  public void testColors3() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}");
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(tensor);
    assertEquals(colorDataIndexed.apply(RealScalar.of(1.9)), tensor.get(1));
    assertEquals(colorDataIndexed.apply(RealScalar.of(1.1)), tensor.get(1));
    assertEquals(colorDataIndexed.apply(RealScalar.of(0.9)), tensor.get(0));
    final Color ref0 = new Color(1, 2, 3, 4);
    assertEquals(colorDataIndexed.getColor(0), ref0);
    final Color ref1 = new Color(5, 6, 7, 8);
    assertEquals(colorDataIndexed.getColor(1), ref1);
    final Color ref2 = new Color(9, 10, 11, 12);
    assertEquals(colorDataIndexed.getColor(2), ref2);
  }

  @Test
  public void testDerive() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = Serialization.copy(StrictColorDataIndexed.of(tensor));
    colorDataIndexed = colorDataIndexed.deriveWithAlpha(255);
    final Color ref0 = new Color(1, 2, 3, 255);
    assertEquals(colorDataIndexed.getColor(0), ref0);
    final Color ref1 = new Color(5, 6, 7, 255);
    assertEquals(colorDataIndexed.getColor(1), ref1);
  }

  @Test
  public void testEmpty() throws ClassNotFoundException, IOException {
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(Tensors.empty());
    Serialization.copy(colorDataIndexed.deriveWithAlpha(128));
  }

  @Test
  public void testColors() {
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(Color.BLUE, Color.RED, Color.BLACK);
    assertEquals(colorDataIndexed.getColor(0), Color.BLUE);
    assertEquals(colorDataIndexed.getColor(1), Color.RED);
    assertEquals(colorDataIndexed.getColor(2), Color.BLACK);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> colorDataIndexed.getColor(3));
  }

  @Test
  public void testFails() {
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(Color.BLUE, Color.RED);
    assertThrows(Exception.class, () -> colorDataIndexed.apply(Quantity.of(1, "m")));
    assertThrows(Exception.class, () -> colorDataIndexed.apply(Quantity.of(Double.NaN, "m")));
    assertThrows(Exception.class, () -> colorDataIndexed.apply(Quantity.of(Double.POSITIVE_INFINITY, "m")));
  }

  @Test
  public void testFailCreate() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}}");
    assertThrows(TensorRuntimeException.class, () -> StrictColorDataIndexed.of(tensor));
  }

  @Test
  public void testFailCreateScalar() {
    assertThrows(TensorRuntimeException.class, () -> StrictColorDataIndexed.of(RealScalar.ONE));
  }

  @Test
  public void testFailExtract() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(tensor);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> colorDataIndexed.getColor(-1));
  }
}
