// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Power;

class IntervalTest {
  @Test
  void testInverse1() {
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), Interval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))), //
        Tensors.of(RealScalar.of(2), RealScalar.of(1)));
    Tensor tensor = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    // System.out.println(Pretty.of(tensor.map(Round._3)));
    Pretty.of(tensor);
    // System.out.println();
  }

  @Test
  void testInverse2() {
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), Interval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))), //
        Tensors.of(RealScalar.of(2), Interval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))));
    Tensor tensor = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    // System.out.println(Pretty.of(tensor.map(Round._3)));
    Pretty.of(tensor);
    // System.out.println();
  }

  @Test
  void testAbs() {
    assertEquals(Abs.FUNCTION.apply(Interval.of(-4, 1)), Interval.of(0, 4));
    assertEquals(Abs.FUNCTION.apply(Interval.of(-4, -2)), Interval.of(2, 4));
  }

  @Test
  void testAbsSquared() {
    assertEquals(AbsSquared.FUNCTION.apply(Interval.of(-4, 1)), Interval.of(0, 16));
    assertEquals(AbsSquared.FUNCTION.apply(Interval.of(-4, -2)), Interval.of(4, 16));
  }

  @Test
  void testExact() {
    assertTrue(ExactScalarQ.of(Interval.of(-4, 1)));
    assertFalse(ExactScalarQ.of(Interval.of(-4.3, 1.4)));
    assertFalse(ExactScalarQ.of(Interval.of(-4, 1.3)));
    assertFalse(ExactScalarQ.of(Interval.of(-4.1, 1)));
  }

  @Test
  void testRound() {
    Scalar scalar = Interval.of(2.3, 5.6);
    Round.FUNCTION.apply(scalar);
    Ceiling.FUNCTION.apply(scalar);
    Floor.FUNCTION.apply(scalar);
    Sign.FUNCTION.apply(scalar);
  }

  @Test
  void testReciprocalFail() {
    assertThrows(Throw.class, () -> Interval.of(-2.3, 5.6).reciprocal());
  }

  @Test
  void testPowerFail() {
    assertThrows(Throw.class, () -> Power.of(Interval.of(-2.3, 5.6), 2.3));
  }

  @Test
  void testNumberFail() {
    assertThrows(Throw.class, () -> Interval.of(-2.3, 5.6).number());
  }

  @Test
  void testExp() {
    Exp.FUNCTION.apply(Interval.of(-4, 1));
    Log.FUNCTION.apply(Interval.of(2, 3));
    Interval.of(-4, 1).hashCode();
  }

  @Test
  void testPower() {
    Power.of(Interval.of(-4, 1), 7);
  }
}
