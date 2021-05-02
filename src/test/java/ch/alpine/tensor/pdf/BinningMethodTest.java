// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BinningMethodTest extends TestCase {
  public void testRice() {
    Scalar width = BinningMethod.RICE.apply(Tensors.vector(2, 4, 3, 6));
    ExactScalarQ.require(width);
    assertEquals(width, RealScalar.ONE);
  }

  public void testRoot() {
    Scalar width = BinningMethod.SQRT.apply(Tensors.vector(2, 4, 3, 6));
    assertEquals(width, RealScalar.of(2));
  }

  public void testQuantity() {
    Tensor samples = QuantityTensor.of(Tensors.vector(1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3), "Apples");
    for (BinningMethod binningMethod : BinningMethod.values()) {
      Scalar width = binningMethod.apply(samples);
      assertTrue(width instanceof Quantity);
      Scalar value = QuantityMagnitude.singleton("Apples").apply(width);
      assertTrue(Sign.isPositive(value));
    }
  }

  public void testFail() {
    for (BinningMethod binningMethod : BinningMethod.values())
      AssertFail.of(() -> binningMethod.apply(Tensors.empty()));
  }
}
