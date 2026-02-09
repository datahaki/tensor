// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;

class KroneckerDeltaTest {
  /** @param scalar
   * @return function that maps input to 1 if input matches scalar, otherwise gives 0 */
  public static ScalarUnaryOperator function(Scalar scalar) {
    return value -> KroneckerDelta.of(scalar, value);
  }

  @Test
  void testKroneckerDelta() {
    final Scalar one = RealScalar.ONE;
    assertEquals(KroneckerDelta.of(), one);
    assertEquals(KroneckerDelta.of(1), one);
    assertEquals(KroneckerDelta.of(1, 2), RealScalar.ZERO);
    assertEquals(KroneckerDelta.of(3, 3, 3), one);
    assertEquals(KroneckerDelta.of(3, 3, 1), RealScalar.ZERO);
  }

  @Test
  void testFunction() {
    Tensor vector = Tensors.vector(0, 3, 255, 0, 255, -43, 3, 0, 255, 0, 225);
    Tensor res = vector.maps(function(RealScalar.of(255)));
    assertEquals(res, Tensors.fromString("{0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0}"));
  }

  @Test
  void testStream() {
    assertEquals(KroneckerDelta.of(Tensors.vector(3, 1, 3).stream()), RealScalar.ZERO);
    assertEquals(KroneckerDelta.of(Tensors.vector(3, 3, 3).stream()), RealScalar.ONE);
    assertEquals(KroneckerDelta.of(Tensors.empty().stream()), RealScalar.ONE);
  }

  @Test
  void testStream2() {
    assertEquals(KroneckerDelta.of("abc", "cde"), RealScalar.ZERO);
    assertEquals(KroneckerDelta.of("abc", "abc"), RealScalar.ONE);
    assertEquals(KroneckerDelta.of("abc"), RealScalar.ONE);
    assertEquals(KroneckerDelta.of(), RealScalar.ONE);
  }
}
