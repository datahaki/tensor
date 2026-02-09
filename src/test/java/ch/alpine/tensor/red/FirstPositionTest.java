// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

class FirstPositionTest {
  @Test
  void testSimple() {
    Tensor tensor = Tensors.vector(5, 6, 7, 8, 9);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(5)).orElseThrow(), 0);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(6)).orElseThrow(), 1);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(7)).orElseThrow(), 2);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(8)).orElseThrow(), 3);
    assertEquals(FirstPosition.of(tensor, RealScalar.of(9)).orElseThrow(), 4);
    assertFalse(FirstPosition.of(tensor, RealScalar.of(10)).isPresent());
  }

  @Test
  void testQuantity() {
    Tensor tensor = Tensors.vector(5, 6, 7, 8, 9.0).maps(s -> Quantity.of(s, "km"));
    assertFalse(FirstPosition.of(tensor, RealScalar.of(5)).isPresent());
    assertEquals(FirstPosition.of(tensor, Quantity.of(8, "km")).orElseThrow(), 3);
    assertEquals(FirstPosition.of(tensor, Quantity.of(9, "km")).orElseThrow(), 4);
    assertFalse(FirstPosition.of(tensor, RealScalar.of(10)).isPresent());
  }

  @Test
  void testMatrix() {
    Tensor tensor = HilbertMatrix.of(10);
    assertEquals(FirstPosition.of(tensor, tensor.get(3)).orElseThrow(), 3);
  }

  @Test
  void testEmpty() {
    FirstPosition.of(Tensors.empty(), Pi.VALUE);
  }

  @Test
  void testFailScalar() {
    assertThrows(Throw.class, () -> FirstPosition.of(RealScalar.of(7), RealScalar.of(7)));
  }

  @Test
  void testFailTensorNull() {
    assertThrows(NullPointerException.class, () -> FirstPosition.of(null, RealScalar.of(7)));
  }

  @Test
  void testFailElementNull() {
    assertThrows(NullPointerException.class, () -> FirstPosition.of(Tensors.vector(5, 6, 7, 8, 9), null));
  }

  @Test
  void testFailEmptyNull() {
    assertThrows(NullPointerException.class, () -> FirstPosition.of(Tensors.empty(), null));
  }
}
