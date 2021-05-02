// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FirstPositionTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensors.vector(5, 6, 7, 8, 9);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(5)).getAsInt(), 0);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(6)).getAsInt(), 1);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(7)).getAsInt(), 2);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(8)).getAsInt(), 3);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(9)).getAsInt(), 4);
    assertFalse(FirstPosition.of(tensor, RealScalar.of(10)).isPresent());
  }

  public void testQuantity() {
    Tensor tensor = Tensors.vector(5, 6, 7, 8, 9.0).map(s -> Quantity.of(s, "km"));
    assertFalse(FirstPosition.of(tensor, RealScalar.of(5)).isPresent());
    assertEquals(FirstPosition.of(tensor, Quantity.of(8, "km")).getAsInt(), 3);
    assertEquals(FirstPosition.of(tensor, Quantity.of(9, "km")).getAsInt(), 4);
    assertFalse(FirstPosition.of(tensor, RealScalar.of(10)).isPresent());
  }

  public void testMatrix() {
    Tensor tensor = HilbertMatrix.of(10);
    assertEquals(FirstPosition.of(tensor, tensor.get(3)).getAsInt(), 3);
  }

  public void testEmpty() {
    FirstPosition.of(Tensors.empty(), Pi.VALUE);
  }

  public void testFailScalar() {
    AssertFail.of(() -> FirstPosition.of(RealScalar.of(7), RealScalar.of(7)));
  }

  public void testFailTensorNull() {
    AssertFail.of(() -> FirstPosition.of(null, RealScalar.of(7)));
  }

  public void testFailElementNull() {
    AssertFail.of(() -> FirstPosition.of(Tensors.vector(5, 6, 7, 8, 9), null));
  }

  public void testFailEmptyNull() {
    AssertFail.of(() -> FirstPosition.of(Tensors.empty(), null));
  }
}
