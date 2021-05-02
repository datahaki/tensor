// code by jph
package ch.alpine.tensor.img;

import java.awt.Color;
import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StrictColorDataIndexedTest extends TestCase {
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

  public void testDerive() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = Serialization.copy(StrictColorDataIndexed.of(tensor));
    colorDataIndexed = colorDataIndexed.deriveWithAlpha(255);
    final Color ref0 = new Color(1, 2, 3, 255);
    assertEquals(colorDataIndexed.getColor(0), ref0);
    final Color ref1 = new Color(5, 6, 7, 255);
    assertEquals(colorDataIndexed.getColor(1), ref1);
  }

  public void testEmpty() throws ClassNotFoundException, IOException {
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(Tensors.empty());
    Serialization.copy(colorDataIndexed.deriveWithAlpha(128));
  }

  public void testFailCreate() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}}");
    AssertFail.of(() -> StrictColorDataIndexed.of(tensor));
  }

  public void testFailCreateScalar() {
    AssertFail.of(() -> StrictColorDataIndexed.of(RealScalar.ONE));
  }

  public void testFailExtract() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    ColorDataIndexed colorDataIndexed = StrictColorDataIndexed.of(tensor);
    AssertFail.of(() -> colorDataIndexed.getColor(-1));
  }
}
