// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class InvertUnlessZeroTest extends TestCase {
  public void testVector() {
    Tensor tensor = InvertUnlessZero.of(Tensors.vector(1, 0, 2));
    assertEquals(tensor, Tensors.vector(1, 0, 0.5));
    ExactTensorQ.require(tensor);
  }

  public void testQuantity() {
    Scalar scalar = Quantity.of(-2, "m");
    Scalar invert = InvertUnlessZero.FUNCTION.apply(scalar);
    ExactScalarQ.require(invert);
    assertEquals(invert, Quantity.of(RationalScalar.HALF, "m^-1").negate());
  }

  public void testQuantityFail() {
    Scalar scalar = Quantity.of(0, "m");
    Scalar invert = InvertUnlessZero.FUNCTION.apply(scalar);
    ExactScalarQ.require(invert);
    assertEquals(invert, Quantity.of(RealScalar.ZERO, "m^-1"));
  }
}
