// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;

class NumeratorTest {
  @Test
  void testSimple() {
    assertEquals(Numerator.FUNCTION.apply(RationalScalar.of(+2, 3)), RealScalar.of(+2));
    assertEquals(Numerator.FUNCTION.apply(RationalScalar.of(-2, 3)), RealScalar.of(-2));
    assertEquals(Numerator.FUNCTION.apply(Pi.HALF), Pi.HALF);
  }

  @Test
  void testPrimitives() {
    assertEquals(Numerator.intValueExact(RationalScalar.of(-2, 3)), -2);
    assertEquals(Numerator.longValueExact(RationalScalar.of(-2, 3)), -2L);
  }
}
