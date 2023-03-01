// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;

class IntegerQTest {
  @Test
  void testPositive() {
    assertTrue(IntegerQ.of(Scalars.fromString("9/3")));
    assertTrue(IntegerQ.of(Scalars.fromString("-529384765923478653476593847659876237486")));
    assertTrue(IntegerQ.of(ComplexScalar.of(-2, 0)));
  }

  @Test
  void testNegative() {
    assertFalse(IntegerQ.of(Scalars.fromString("9.0")));
    assertFalse(IntegerQ.of(ComplexScalar.of(2, 3)));
    assertFalse(IntegerQ.of(Scalars.fromString("abc")));
  }

  @Test
  void testRequire() {
    Scalar scalar = RealScalar.of(2);
    assertSame(IntegerQ.require(scalar), scalar);
  }

  @Test
  void testRequireFail() {
    assertThrows(Throw.class, () -> IntegerQ.require(RealScalar.of(0.2)));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> IntegerQ.of(null));
  }
}
