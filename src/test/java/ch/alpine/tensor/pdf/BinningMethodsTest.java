// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.sca.Sign;

class BinningMethodsTest {
  @Test
  void testRice() {
    Scalar width = BinningMethods.RICE.apply(Tensors.vector(2, 4, 3, 6));
    ExactScalarQ.require(width);
    assertEquals(width, RealScalar.ONE);
  }

  @Test
  void testRoot() {
    Scalar width = BinningMethods.SQRT.apply(Tensors.vector(2, 4, 3, 6));
    assertEquals(width, RealScalar.of(2));
  }

  @ParameterizedTest
  @EnumSource
  void testQuantity(BinningMethods binningMethod) {
    Tensor samples = QuantityTensor.of(Tensors.vector(1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3), "Apples");
    Scalar width = binningMethod.apply(samples);
    assertInstanceOf(Quantity.class, width);
    Scalar value = QuantityMagnitude.singleton("Apples").apply(width);
    assertTrue(Sign.isPositive(value));
  }

  @ParameterizedTest
  @EnumSource
  void testFail(BinningMethods binningMethod) {
    assertThrows(Exception.class, () -> binningMethod.apply(Tensors.empty()));
  }
}
