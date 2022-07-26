// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.qty.Quantity;

class ParzenWindowTest {
  @Test
  void testSimple() {
    assertEquals(ParzenWindow.FUNCTION.apply(RationalScalar.of(1, 10)), RationalScalar.of(101, 125));
    assertEquals(ParzenWindow.FUNCTION.apply(RationalScalar.of(3, 10)), RationalScalar.of(16, 125));
  }

  @Test
  void testSemiExact() {
    Scalar scalar = ParzenWindow.FUNCTION.apply(RationalScalar.HALF);
    assertTrue(Scalars.isZero(scalar));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testOutside() {
    Scalar scalar = ParzenWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  @Test
  void testExact() {
    Scalar scalar = ParzenWindow.FUNCTION.apply(RationalScalar.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> ParzenWindow.FUNCTION.apply(Quantity.of(0, "s")));
    assertThrows(Throw.class, () -> ParzenWindow.FUNCTION.apply(Quantity.of(2, "s")));
  }
}
