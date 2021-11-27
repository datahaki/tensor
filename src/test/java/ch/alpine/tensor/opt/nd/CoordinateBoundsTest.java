// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CoordinateBoundsTest extends TestCase {
  public void testSimple() {
    // CoordinateBoundingBox[{{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 1}}]
    Tensor tensor = Tensors.fromString("{{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 1}}");
    CoordinateBoundingBox box = CoordinateBounds.of(tensor);
    assertEquals(box.getClip(0), Clips.interval(0, 4));
    assertEquals(box.getClip(1), Clips.interval(1, 2));
    // assertEquals(minMax.min(), Tensors.vector(1, 5, -6));
    // assertEquals(minMax.max(), Tensors.vector(4, 9, 3));
  }

  public void testFail() {
    Tensor tensor = Tensors.fromString("{{1, 9, 3}, {4, 5}}");
    AssertFail.of(() -> CoordinateBounds.of(tensor));
  }

  public void testFailEmpty() {
    // AssertFail.of(() -> CoordinateBoundingBox.of(Tensors.empty()));
    AssertFail.of(() -> CoordinateBounds.of(Tensors.empty()));
  }

  public void testFailScalar() {
    AssertFail.of(() -> CoordinateBounds.of(RealScalar.ZERO));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(CoordinateBounds.of(Tensors.vector(2, 3, 9), Tensors.vector(12, 23, 11)));
  }

  public void testNulls() {
    AssertFail.of(() -> CoordinateBounds.of(null, Tensors.vector(3)));
    AssertFail.of(() -> CoordinateBounds.of(Tensors.vector(3), null));
  }

  public void testFail0() {
    AssertFail.of(() -> CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9, 3)));
  }

  public void testFail2() {
    AssertFail.of(() -> CoordinateBounds.of(Tensors.vector(-2, 10), Tensors.vector(8, 9)));
  }

  public void testFail3() {
    CoordinateBounds.of(Tensors.vector(0), Tensors.fromString("{2}"));
    AssertFail.of(() -> CoordinateBounds.of(Tensors.vector(0), Tensors.fromString("{2[m]}")));
  }
}
