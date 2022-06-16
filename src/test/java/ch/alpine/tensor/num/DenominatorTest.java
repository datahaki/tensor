// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;

class DenominatorTest {
  @Test
  void testSimple() {
    assertEquals(Denominator.FUNCTION.apply(RationalScalar.of(+2, 3)), RealScalar.of(3));
    assertEquals(Denominator.FUNCTION.apply(RationalScalar.of(-2, 3)), RealScalar.of(3));
    assertEquals(Denominator.FUNCTION.apply(Pi.HALF), RealScalar.ONE);
  }

  @Test
  void testGaussScalar() {
    assertEquals(Denominator.FUNCTION.apply(GaussScalar.of(5, 17)), GaussScalar.of(1, 17));
  }
}
