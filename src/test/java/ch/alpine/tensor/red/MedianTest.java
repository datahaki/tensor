// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;

class MedianTest {
  @Test
  void testEven() {
    /* Median[{1, 2, 3, 4, 5, 6, 7, 8}] == 9/2 */
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6, 7, 8);
    assertEquals(Median.of(vector), Rational.of(9, 2));
    assertEquals(Median.of(Tensors.fromString("{1, 2}")), Rational.of(3, 2));
  }

  @Test
  void testOdd() {
    /* Median[{1, 2, 3, 4, 5, 6, 7}] == 4 */
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6, 7);
    assertEquals(Median.of(vector), RealScalar.of(4));
    assertEquals(Median.of(Tensors.fromString("{1}")), Rational.of(1, 1));
  }

  @Test
  void testQuantity() {
    // confirmed with mathematica:
    // Median[{1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3}] == 3
    Tensor tensor = QuantityTensor.of(Tensors.vector(1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3), "Apples");
    Tensor s = Median.of(tensor);
    assertEquals(s, Quantity.of(3, "Apples"));
  }

  @Test
  void testEmpty() {
    assertThrows(IllegalArgumentException.class, () -> Median.of(Tensors.empty()));
    assertThrows(Throw.class, () -> Median.of(Pi.VALUE));
  }

  @Test
  void testUnorderedFail() {
    assertThrows(Throw.class, () -> Median.ofSorted(Tensors.vector(3, 2, 1)));
    assertThrows(Throw.class, () -> Median.ofSorted(Tensors.vector(1, 2, 1)));
  }
}
