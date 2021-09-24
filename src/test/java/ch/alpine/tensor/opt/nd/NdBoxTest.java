// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.IOException;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NdBoxTest extends TestCase {
  public void testProject() {
    NdBox ndBox = NdBox.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    assertEquals(ndBox.clip(Tensors.vector(0, 0)), Tensors.vector(2, 3));
    assertEquals(ndBox.clip(Tensors.vector(0, 20)), Tensors.vector(2, 20));
    assertEquals(ndBox.clip(Tensors.vector(0, 40)), Tensors.vector(2, 23));
    assertEquals(ndBox.clip(Tensors.vector(3, 40)), Tensors.vector(3, 23));
    assertEquals(ndBox.clip(Tensors.vector(4, 10)), Tensors.vector(4, 10));
    assertEquals(ndBox.clip(Tensors.vector(14, 10)), Tensors.vector(12, 10));
  }

  public void testSimple() {
    NdBox ndBox = NdBox.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    assertEquals(ndBox.min(), Tensors.vector(2, 3));
    assertEquals(ndBox.max(), Tensors.vector(12, 23));
  }

  public void testUnits() {
    NdBox ndBox = NdBox.of( //
        Tensors.fromString("{1[m], 2[m], 3[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    Tensor tensor = TestHelper.sample(ndBox);
    assertTrue(ndBox.isInside(tensor));
    ndBox.requireInside(tensor);
  }

  public void testDegenerate() {
    NdBox ndBox = NdBox.of( //
        Tensors.fromString("{1[m], 2[m], 4[m]}"), //
        Tensors.fromString("{2[m], 3[m], 4[m]}"));
    Tensor tensor = TestHelper.sample(ndBox);
    assertEquals(ExactScalarQ.require(tensor.Get(2)), Quantity.of(4, "m"));
    assertTrue(ndBox.isInside(tensor));
    ndBox.requireInside(tensor);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(NdBox.of(Tensors.vector(2, 3, 9), Tensors.vector(12, 23, 11)));
  }

  public void testNulls() {
    AssertFail.of(() -> NdBox.of(null, Tensors.vector(3)));
    AssertFail.of(() -> NdBox.of(Tensors.vector(3), null));
  }

  public void testEmpty() {
    NdBox ndBox = NdBox.of(Tensors.empty(), Tensors.empty());
    assertEquals(ndBox.dimensions(), 0);
    assertEquals(ndBox.min(), Tensors.empty());
    assertEquals(ndBox.max(), Tensors.empty());
  }

  public void testFail0() {
    AssertFail.of(() -> NdBox.of(Tensors.vector(-2, -3), Tensors.vector(8, 9, 3)));
  }

  public void testFail1() {
    NdBox ndBox = NdBox.of(Tensors.vector(2, 3), Tensors.vector(12, 23));
    ndBox.requireInside(Tensors.vector(4, 3));
    AssertFail.of(() -> ndBox.requireInside(Tensors.vector(14, 3)));
  }

  public void testFail2() {
    AssertFail.of(() -> NdBox.of(Tensors.vector(-2, 10), Tensors.vector(8, 9)));
  }

  public void testFail3() {
    NdBox.of(Tensors.vector(0), Tensors.fromString("{2}"));
    AssertFail.of(() -> NdBox.of(Tensors.vector(0), Tensors.fromString("{2[m]}")));
  }
}
