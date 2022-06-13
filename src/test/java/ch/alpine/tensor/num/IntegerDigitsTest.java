// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;

class IntegerDigitsTest {
  @Test
  void testSimple() {
    assertEquals(IntegerDigits.of(RealScalar.of(+321)), Tensors.vector(3, 2, 1));
    assertEquals(IntegerDigits.of(RealScalar.of(-321)), Tensors.vector(3, 2, 1));
    assertEquals(IntegerDigits.of(RealScalar.of(+123456789)), Tensors.vector(1, 2, 3, 4, 5, 6, 7, 8, 9));
    assertEquals(IntegerDigits.of(RealScalar.of(-123456789)), Tensors.vector(1, 2, 3, 4, 5, 6, 7, 8, 9));
  }

  @Test
  void testExact() {
    assertEquals(IntegerDigits.of(Scalars.fromString("123456789012345678901234567890")), Tensors.vector(Arrays.asList( //
        1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0)));
  }

  @Test
  void testBase() {
    assertEquals(IntegerDigits.base(2).apply(Scalars.fromString("9")), Tensors.vector(Arrays.asList(1, 0, 0, 1)));
    assertEquals(IntegerDigits.base(2).apply(Scalars.fromString("11")), Tensors.vector(Arrays.asList(1, 0, 1, 1)));
    assertEquals(IntegerDigits.base(3).apply(Scalars.fromString("11")), Tensors.vector(Arrays.asList(1, 0, 2)));
  }

  @Test
  void testBaseFail() {
    assertThrows(IllegalArgumentException.class, () -> IntegerDigits.base(1));
    assertThrows(IllegalArgumentException.class, () -> IntegerDigits.base(0));
    assertThrows(IllegalArgumentException.class, () -> IntegerDigits.base(-1));
  }

  @Test
  void testZero() {
    assertEquals(IntegerDigits.of(RealScalar.ZERO), Tensors.vector(Arrays.asList()));
  }

  @Test
  void testPrecisionFail() {
    assertThrows(TensorRuntimeException.class, () -> IntegerDigits.of(RealScalar.of(1.0)));
  }

  @Test
  void testRationalFail() {
    assertThrows(TensorRuntimeException.class, () -> IntegerDigits.of(RationalScalar.of(10, 3)));
  }
}
