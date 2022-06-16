// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Clips;

class CoordinateBoundsTest {
  @Test
  void testSimple() {
    // CoordinateBoundingBox[{{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 1}}]
    Tensor tensor = Tensors.fromString("{{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 1}}");
    CoordinateBoundingBox box = CoordinateBounds.of(tensor);
    assertEquals(box.getClip(0), Clips.interval(0, 4));
    assertEquals(box.getClip(1), Clips.interval(1, 2));
    // assertEquals(minMax.min(), Tensors.vector(1, 5, -6));
    // assertEquals(minMax.max(), Tensors.vector(4, 9, 3));
  }

  @Test
  void testFail() {
    Tensor tensor = Tensors.fromString("{{1, 9, 3}, {4, 5}}");
    assertThrows(IllegalArgumentException.class, () -> CoordinateBounds.of(tensor));
  }

  @Test
  void testFailEmpty() {
    // assertThrows(TensorRuntimeException.class, () -> CoordinateBoundingBox.of(Tensors.empty()));
    assertThrows(NoSuchElementException.class, () -> CoordinateBounds.of(Tensors.empty()));
  }

  @Test
  void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> CoordinateBounds.of(RealScalar.ZERO));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(CoordinateBounds.of(Tensors.vector(2, 3, 9), Tensors.vector(12, 23, 11)));
  }

  @Test
  void testNulls() {
    assertThrows(NullPointerException.class, () -> CoordinateBounds.of(null, Tensors.vector(3)));
    assertThrows(NullPointerException.class, () -> CoordinateBounds.of(Tensors.vector(3), null));
  }

  @Test
  void testFail0() {
    assertThrows(IllegalArgumentException.class, () -> CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9, 3)));
  }

  @Test
  void testFail2() {
    assertThrows(TensorRuntimeException.class, () -> CoordinateBounds.of(Tensors.vector(-2, 10), Tensors.vector(8, 9)));
  }

  @Test
  void testFail3() {
    CoordinateBounds.of(Tensors.vector(0), Tensors.fromString("{2}"));
    assertThrows(TensorRuntimeException.class, () -> CoordinateBounds.of(Tensors.vector(0), Tensors.fromString("{2[m]}")));
  }
}
