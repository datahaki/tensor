// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.usr.AssertFail;

public class MedianTest {
  @Test
  public void testEven() {
    /** Median[{1, 2, 3, 4, 5, 6, 7, 8}] == 9/2 */
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6, 7, 8);
    assertEquals(Median.of(vector), RationalScalar.of(9, 2));
    assertEquals(Median.of(Tensors.fromString("{1, 2}")), RationalScalar.of(3, 2));
  }

  @Test
  public void testOdd() {
    /** Median[{1, 2, 3, 4, 5, 6, 7}] == 4 */
    Tensor vector = Tensors.vector(1, 2, 3, 4, 5, 6, 7);
    assertEquals(Median.of(vector), RealScalar.of(4));
    assertEquals(Median.of(Tensors.fromString("{1}")), RationalScalar.of(1, 1));
  }

  @Test
  public void testQuantity() {
    // confirmed with mathematica:
    // Median[{1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3}] == 3
    Tensor tensor = QuantityTensor.of(Tensors.vector(1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3), "Apples");
    Tensor s = Median.of(tensor);
    assertEquals(s, Quantity.of(3, "Apples"));
  }

  @Test
  public void testEmpty() {
    AssertFail.of(() -> Median.of(Tensors.empty()));
    AssertFail.of(() -> Median.of(Pi.VALUE));
  }

  @Test
  public void testUnorderedFail() {
    AssertFail.of(() -> Median.ofSorted(Tensors.vector(3, 2, 1)));
    AssertFail.of(() -> Median.ofSorted(Tensors.vector(1, 2, 1)));
  }
}
