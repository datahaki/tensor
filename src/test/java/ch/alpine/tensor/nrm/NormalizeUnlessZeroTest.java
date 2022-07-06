// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;

class NormalizeUnlessZeroTest {
  @Test
  void testNormalizeNaN() {
    Tensor vector = Tensors.of(RealScalar.ONE, DoubleScalar.INDETERMINATE, RealScalar.ONE);
    assertThrows(Throw.class, () -> NormalizeUnlessZero.with(Vector2Norm::of).apply(vector));
  }

  @Test
  void testNormalizeTotal() {
    TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(Total::ofVector);
    assertTrue(tensorUnaryOperator.toString().startsWith("NormalizeUnlessZero"));
    Tensor tensor = tensorUnaryOperator.apply(Tensors.vector(-1, 3, 2));
    assertEquals(tensor, Tensors.fromString("{-1/4, 3/4, 1/2}"));
    Tensor vector = Tensors.vector(-1, 3, -2);
    Tensor result = tensorUnaryOperator.apply(vector);
    assertEquals(vector, result);
    ExactTensorQ.require(result);
  }

  @Test
  void testQuantity() {
    TensorUnaryOperator tuo = NormalizeUnlessZero.with(Vector2Norm::of);
    Tensor one = Tensors.of(Quantity.of(2, "m"));
    assertEquals(tuo.apply(one), Tensors.vector(1));
    Tensor zer = Tensors.of(Quantity.of(0, "m"));
    assertEquals(tuo.apply(zer), Array.zeros(1));
  }

  @Test
  void testUnitless() {
    TensorUnaryOperator tuo = NormalizeUnlessZero.with(Vector2Norm::of);
    Tensor one = Tensors.of(Quantity.of(0, "m"), Quantity.of(0, "m"));
    assertEquals(tuo.apply(one), Array.zeros(2));
  }

  @Test
  void testMixedUnit1Fail() {
    TensorUnaryOperator tuo = NormalizeUnlessZero.with(Vector2Norm::of);
    Tensor one = Tensors.of(Quantity.of(0, "m"), Quantity.of(1, "s"));
    assertThrows(Throw.class, () -> tuo.apply(one));
  }

  @Test
  void testMixedUnit2Fail() {
    TensorUnaryOperator tuo = NormalizeUnlessZero.with(Vector2Norm::of);
    Tensor one = Tensors.of(Quantity.of(0, "m"), Quantity.of(0, "s"));
    assertThrows(Throw.class, () -> tuo.apply(one));
  }
}
