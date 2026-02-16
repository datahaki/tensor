// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.qty.Quantity;

class InvertUnlessZeroTest {
  @Test
  void testVector() {
    Tensor tensor = Tensors.vector(1, 0, 2).maps(InvertUnlessZero.FUNCTION);
    assertEquals(tensor, Tensors.vector(1, 0, 0.5));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testQuantity() {
    Scalar scalar = Quantity.of(-2, "m");
    Scalar invert = InvertUnlessZero.FUNCTION.apply(scalar);
    ExactScalarQ.require(invert);
    assertEquals(invert, Quantity.of(Rational.HALF, "m^-1").negate());
  }

  @Test
  void testQuantityFail() {
    Scalar scalar = Quantity.of(0, "m");
    Scalar invert = InvertUnlessZero.FUNCTION.apply(scalar);
    ExactScalarQ.require(invert);
    assertEquals(invert, Quantity.of(RealScalar.ZERO, "m^-1"));
  }
}
