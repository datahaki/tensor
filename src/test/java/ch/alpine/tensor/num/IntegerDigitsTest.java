// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.pow.Power;

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
    assertEquals(IntegerDigits.of(RealScalar.ZERO), Tensors.vector(List.of()));
  }

  @Test
  void testRiddle() {
    int c = 10;
    while (c < 1000) {
      Scalar scalar = Power.of(c, 2);
      Tensor tensor = IntegerDigits.of(scalar);
      Tensor list = Drop.head(tensor, tensor.length() - 3);
      if (Tally.of(list).size() == 1) {
        assertEquals(c, 38);
        assertEquals(scalar, RealScalar.of(1444));
        assertEquals(list, Tensors.vector(4, 4, 4));
        break;
      }
      ++c;
    }
  }

  @Test
  void testPrecisionFail() {
    assertThrows(Throw.class, () -> IntegerDigits.of(RealScalar.of(1.0)));
  }

  @Test
  void testRationalFail() {
    assertThrows(Throw.class, () -> IntegerDigits.of(RationalScalar.of(10, 3)));
  }
}
