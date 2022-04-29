// code by jph
package ch.alpine.tensor.sca.win;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.sca.Chop;

public class BartlettWindowTest {
  @Test
  public void testZero() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ONE);
  }

  @Test
  public void testExact() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RationalScalar.of(3, 3465));
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RationalScalar.of(1153, 1155));
  }

  @Test
  public void testExact2() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RationalScalar.of(2, 5));
    ExactScalarQ.require(scalar);
    assertFalse(Scalars.isZero(scalar));
  }

  @Test
  public void testContinuous() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.of(0.499999999));
    Chop._07.requireZero(scalar);
  }

  @Test
  public void testSemiExact() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RationalScalar.HALF);
    assertTrue(Scalars.isZero(scalar));
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testOutside() {
    Scalar scalar = BartlettWindow.FUNCTION.apply(RealScalar.of(-0.52));
    assertEquals(scalar, RealScalar.ZERO);
  }
}
