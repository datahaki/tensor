// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.IOException;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BoxTest extends TestCase {
  public void testProject() {
    Box box = Box.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    assertEquals(box.clip(Tensors.vector(0, 0)), Tensors.vector(2, 3));
    assertEquals(box.clip(Tensors.vector(0, 20)), Tensors.vector(2, 20));
    assertEquals(box.clip(Tensors.vector(0, 40)), Tensors.vector(2, 23));
    assertEquals(box.clip(Tensors.vector(3, 40)), Tensors.vector(3, 23));
    assertEquals(box.clip(Tensors.vector(4, 10)), Tensors.vector(4, 10));
    assertEquals(box.clip(Tensors.vector(14, 10)), Tensors.vector(12, 10));
    AssertFail.of(() -> box.clip(Tensors.vector(14)));
    AssertFail.of(() -> box.clip(Tensors.vector(14, 10, 3)));
  }

  public void testSimple() {
    Box box = Box.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    assertEquals(box.min(), Tensors.vector(2, 3));
    assertEquals(box.max(), Tensors.vector(12, 23));
  }

  public void testUnits() {
    Box box = Box.of( //
        Tensors.fromString("{1[m], 2[m], 3[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    Tensor tensor = TestHelper.sample(box);
    assertTrue(box.isInside(tensor));
    box.requireInside(tensor);
  }

  public void testMixedUnits() {
    Box box = Box.of( //
        Tensors.fromString("{1[m], 2[s], 3[A]}"), //
        Tensors.fromString("{2[m], 3[s], 4[A]}"));
    Tensor tensor = TestHelper.sample(box);
    assertTrue(box.isInside(tensor));
    box.requireInside(tensor);
  }

  public void testDegenerate() {
    Box box = Box.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    Tensor tensor = TestHelper.sample(box);
    assertEquals(ExactScalarQ.require(tensor.Get(2)), Quantity.of(4, "m"));
    assertTrue(box.isInside(tensor));
    box.requireInside(tensor);
    assertEquals(box.toString(), "[Clip[1[m], 2[m]], Clip[2[m], 3[m]], Clip[4[m], 4[m]]]");
  }

  public void testHashAndEquals() {
    Box box1 = Box.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    Box box2 = Box.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    assertEquals(box1, box2);
    assertEquals(box1.hashCode(), box2.hashCode());
    Box box3 = Box.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 5[m]}"));
    assertFalse(box1.equals(box3));
    assertFalse(box1.hashCode() == box3.hashCode());
    assertFalse(box1.toString().equals(box3.toString()));
    assertFalse(((Object) box1).equals(RealScalar.ONE));
    assertFalse(((Object) box1).equals("abc"));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(Box.of(Tensors.vector(2, 3, 9), Tensors.vector(12, 23, 11)));
  }

  public void testNulls() {
    AssertFail.of(() -> Box.of(null, Tensors.vector(3)));
    AssertFail.of(() -> Box.of(Tensors.vector(3), null));
  }

  public void testEmpty() {
    Box box = Box.of(Tensors.empty(), Tensors.empty());
    assertEquals(box.dimensions(), 0);
    assertEquals(box.min(), Tensors.empty());
    assertEquals(box.max(), Tensors.empty());
  }

  public void testFail0() {
    AssertFail.of(() -> Box.of(Tensors.vector(-2, -3), Tensors.vector(8, 9, 3)));
  }

  public void testFail1() {
    Box box = Box.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    box.requireInside(Tensors.vector(4, 3));
    AssertFail.of(() -> box.requireInside(Tensors.vector(14, 3)));
  }

  public void testFail2() {
    AssertFail.of(() -> Box.of(Tensors.vector(-2, 10), Tensors.vector(8, 9)));
  }

  public void testFail3() {
    Box.of(Tensors.vector(0), Tensors.fromString("{2}"));
    AssertFail.of(() -> Box.of(Tensors.vector(0), Tensors.fromString("{2[m]}")));
  }
}
