// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class CoordinateBoundingBoxTest {
  @Test
  void testProject() throws ClassNotFoundException, IOException {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    Serialization.copy(coordinateBoundingBox);
    assertEquals(coordinateBoundingBox.mapInside(Tensors.vector(0, 0)), Tensors.vector(2, 3));
    assertEquals(coordinateBoundingBox.mapInside(Tensors.vector(0, 20)), Tensors.vector(2, 20));
    assertEquals(coordinateBoundingBox.mapInside(Tensors.vector(0, 40)), Tensors.vector(2, 23));
    assertEquals(coordinateBoundingBox.mapInside(Tensors.vector(3, 40)), Tensors.vector(3, 23));
    assertEquals(coordinateBoundingBox.mapInside(Tensors.vector(4, 10)), Tensors.vector(4, 10));
    assertEquals(coordinateBoundingBox.mapInside(Tensors.vector(14, 10)), Tensors.vector(12, 10));
    assertThrows(IllegalArgumentException.class, () -> coordinateBoundingBox.mapInside(Tensors.vector(14)));
    assertThrows(IllegalArgumentException.class, () -> coordinateBoundingBox.mapInside(Tensors.vector(14, 10, 3)));
  }

  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    assertEquals(coordinateBoundingBox.min(), Tensors.vector(2, 3));
    assertEquals(coordinateBoundingBox.max(), Tensors.vector(12, 23));
    CoordinateBoundingBox cbb2 = Serialization.copy(CoordinateBoundingBox.of(Clips.interval(2, 12), Clips.interval(3, 23)));
    assertEquals(coordinateBoundingBox, cbb2);
  }

  @Test
  void testSame() throws ClassNotFoundException, IOException {
    Clip clip = Clips.absolute(1);
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBoundingBox.of(clip, clip);
    CoordinateBoundingBox lo = coordinateBoundingBox.splitLo(1);
    assertEquals(lo.getClip(0), clip);
    assertTrue(lo.getClip(0) == clip);
    assertEquals(lo.getClip(1), Clips.interval(-1, 0));
    Serialization.copy(coordinateBoundingBox);
  }

  @Test
  void testMedian() {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Tensors.vector(2), Tensors.vector(12));
    CoordinateBoundingBox splitLo = coordinateBoundingBox.splitLo(0);
    assertEquals(splitLo.min(), Tensors.vector(2));
    assertEquals(splitLo.max(), Tensors.vector(7));
    CoordinateBoundingBox splitHi = coordinateBoundingBox.splitHi(0);
    assertEquals(splitHi.min(), Tensors.vector(7));
    assertEquals(splitHi.max(), Tensors.vector(12));
  }

  @Test
  void testUnits() {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[m], 3[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    Tensor tensor = TestHelper.sample(coordinateBoundingBox);
    assertTrue(coordinateBoundingBox.isInside(tensor));
    coordinateBoundingBox.requireInside(tensor);
  }

  @Test
  void testMixedUnits() {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[s], 3[A]}"), //
        Tensors.fromString("{2[m], 3[s], 4[A]}"));
    Tensor tensor = TestHelper.sample(coordinateBoundingBox);
    assertTrue(coordinateBoundingBox.isInside(tensor));
    coordinateBoundingBox.requireInside(tensor);
  }

  @Test
  void testDegenerate() {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    Tensor tensor = TestHelper.sample(coordinateBoundingBox);
    assertEquals(ExactScalarQ.require(tensor.Get(2)), Quantity.of(4, "m"));
    assertTrue(coordinateBoundingBox.isInside(tensor));
    coordinateBoundingBox.requireInside(tensor);
    assertEquals(coordinateBoundingBox.toString(), "[Clip[1[m], 2[m]], Clip[2[m], 3[m]], Clip[4[m], 4[m]]]");
  }

  @Test
  void testHashAndEquals() {
    CoordinateBoundingBox box1 = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    CoordinateBoundingBox box2 = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    assertEquals(box1, box2);
    assertEquals(box1.hashCode(), box2.hashCode());
    CoordinateBoundingBox box3 = CoordinateBounds.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 5[m]}"));
    assertFalse(box1.equals(box3));
    assertFalse(box1.hashCode() == box3.hashCode());
    assertFalse(box1.toString().equals(box3.toString()));
    assertFalse(((Object) box1).equals(RealScalar.ONE));
    assertFalse(((Object) box1).equals("abc"));
  }

  @Test
  void testEmpty() {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Tensors.empty(), Tensors.empty());
    assertEquals(coordinateBoundingBox.dimensions(), 0);
    assertEquals(coordinateBoundingBox.min(), Tensors.empty());
    assertEquals(coordinateBoundingBox.max(), Tensors.empty());
  }

  @Test
  void testFail1() {
    CoordinateBoundingBox box = CoordinateBounds.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    box.requireInside(Tensors.vector(4, 3));
    assertThrows(TensorRuntimeException.class, () -> box.requireInside(Tensors.vector(14, 3)));
  }

  @Test
  void testFail2() {
    // Arrays.asList
    assertThrows(NullPointerException.class, () -> CoordinateBoundingBox.of(Clips.unit(), null));
  }
}
