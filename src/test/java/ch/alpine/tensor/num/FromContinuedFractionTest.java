// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class FromContinuedFractionTest {
  @Test
  void testWithAlt() {
    Tensor vector = Tensors.fromString("{2, 1, 3, 4}");
    Scalar fraction = FromContinuedFraction.of(vector);
    assertEquals(fraction, Scalars.fromString("47/17"));
  }

  @Test
  void testDec() {
    Tensor vector = Tensors.fromString("{2.123, 1}");
    Scalar fraction = FromContinuedFraction.of(vector);
    Tolerance.CHOP.requireClose(fraction, Scalars.fromString("3.123"));
  }

  @Test
  void testEmptyFail() {
    assertThrows(Exception.class, () -> FromContinuedFraction.of(Tensors.empty()));
  }
}
