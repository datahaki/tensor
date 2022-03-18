// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.sca.Sign;

public class BinningMethodTest {
  @Test
  public void testRice() {
    Scalar width = BinningMethod.RICE.apply(Tensors.vector(2, 4, 3, 6));
    ExactScalarQ.require(width);
    assertEquals(width, RealScalar.ONE);
  }

  @Test
  public void testRoot() {
    Scalar width = BinningMethod.SQRT.apply(Tensors.vector(2, 4, 3, 6));
    assertEquals(width, RealScalar.of(2));
  }

  @Test
  public void testQuantity() {
    Tensor samples = QuantityTensor.of(Tensors.vector(1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3), "Apples");
    for (BinningMethod binningMethod : BinningMethod.values()) {
      Scalar width = binningMethod.apply(samples);
      assertTrue(width instanceof Quantity);
      Scalar value = QuantityMagnitude.singleton("Apples").apply(width);
      assertTrue(Sign.isPositive(value));
    }
  }

  @Test
  public void testFail() {
    for (BinningMethod binningMethod : BinningMethod.values())
      assertThrows(Exception.class, () -> binningMethod.apply(Tensors.empty()));
  }
}
