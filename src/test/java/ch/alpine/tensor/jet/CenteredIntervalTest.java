// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
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

class CenteredIntervalTest {
  @Test
  @Disabled
  void testInverse1() {
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), CenteredInterval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))), //
        Tensors.of(RealScalar.of(2), RealScalar.of(1)));
    Tensor tensor = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    // System.out.println(Pretty.of(tensor.map(Round._3)));
    Pretty.of(tensor);
    // System.out.println();
  }

  @Test
  @Disabled
  void testInverse2() {
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), CenteredInterval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))), //
        Tensors.of(RealScalar.of(2), CenteredInterval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))));
    Tensor tensor = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    // System.out.println(Pretty.of(tensor.map(Round._3)));
    Pretty.of(tensor);
    // System.out.println();
  }

  @Test
  @Disabled
  void testAbs() {
    assertEquals(Abs.FUNCTION.apply(CenteredInterval.of(-4, 1)), CenteredInterval.of(0, 4));
    assertEquals(Abs.FUNCTION.apply(CenteredInterval.of(-4, -2)), CenteredInterval.of(2, 4));
  }

  @Test
  @Disabled
  void testAbsSquared() {
    assertEquals(AbsSquared.FUNCTION.apply(CenteredInterval.of(-4, 1)), CenteredInterval.of(0, 16));
    assertEquals(AbsSquared.FUNCTION.apply(CenteredInterval.of(-4, -2)), CenteredInterval.of(4, 16));
  }

  @Test
  @Disabled
  void testExact() {
    assertTrue(ExactScalarQ.of(CenteredInterval.of(-4, 1)));
    assertFalse(ExactScalarQ.of(CenteredInterval.of(-4.3, 1.4)));
    assertFalse(ExactScalarQ.of(CenteredInterval.of(-4, 1.3)));
    assertFalse(ExactScalarQ.of(CenteredInterval.of(-4.1, 1)));
  }

  @Test
  void testRound() {
    Scalar scalar = CenteredInterval.of(2.3, 5.6);
    assertThrows(Exception.class, () -> Round.FUNCTION.apply(scalar));
    assertThrows(Exception.class, () -> Ceiling.FUNCTION.apply(scalar));
    assertThrows(Exception.class, () -> Floor.FUNCTION.apply(scalar));
  }

  @Test
  void testSign() {
    Scalar scalar = CenteredInterval.of(2.3, 5.6);
    Sign.FUNCTION.apply(scalar);
  }

  @Test
  void testReciprocalP() {
    Scalar scalar = CenteredInterval.of(5, 1);
    System.out.println(scalar.reciprocal());
  }

  @Test
  void testReciprocalFail() {
    assertThrows(Throw.class, () -> CenteredInterval.of(-2.3, 5.6).reciprocal());
  }

  @Test
  void testPowerFail() {
    assertThrows(Throw.class, () -> Power.of(CenteredInterval.of(-2.3, 5.6), 2.3));
  }

  @Test
  void testNumberFail() {
    assertThrows(Throw.class, () -> CenteredInterval.of(-2.3, 5.6).number());
  }

  @Test
  @Disabled
  void testExp() {
    Exp.FUNCTION.apply(CenteredInterval.of(-4, 1));
    Log.FUNCTION.apply(CenteredInterval.of(2, 3));
    CenteredInterval.of(-4, 1).hashCode();
  }

  @Test
  @Disabled
  void testPower() {
    Power.of(CenteredInterval.of(-4, 1), 7);
  }

  @Test
  @Disabled
  void test() {
    Scalar x = CenteredInterval.of(10, 2);
    Scalar y = CenteredInterval.of(-8, 7);
    assertEquals(x.add(y), CenteredInterval.of(2, 9));
    assertEquals(x.negate(), CenteredInterval.of(-10, 2));
    ExactScalarQ.require(x);
  }

  @Test
  void testAbs1() {
    Scalar x = CenteredInterval.of(3, 4);
    Scalar y = CenteredInterval.of(3.5, 3.5);
    assertEquals(Abs.FUNCTION.apply(x), y);
  }

  @Test
  @Disabled
  void testAbs2() {
    Scalar x = CenteredInterval.of(7, 4);
    assertEquals(Abs.FUNCTION.apply(x), x);
  }

  @Test
  @Disabled
  void testExp2() {
    Scalar x = CenteredInterval.of(2, 1);
    assertEquals(Exp.FUNCTION.apply(x), x);
  }

  @Test
  @Disabled
  void testDateObject() {
    Scalar do1 = CenteredInterval.of(DateTime.now(), Quantity.of(3, "s"));
    Scalar do2 = CenteredInterval.of(Quantity.of(9, "s"), Quantity.of(1, "s"));
    assertEquals(do1.zero(), Quantity.of(0, "s"));
    assertEquals(do1.one(), RealScalar.ONE);
    do1.add(do2);
    do1.add(Quantity.of(4, "s"));
  }

  @Test
  void testExactFalse() {
    Scalar x = CenteredInterval.of(2, 1);
    assertFalse(ExactScalarQ.of(x));
    assertTrue(FiniteScalarQ.of(x));
  }

  @Test
  void testFiniteFalse() {
    Scalar x = CenteredInterval.of(Clips.interval(Pi.VALUE, DoubleScalar.POSITIVE_INFINITY));
    assertFalse(ExactScalarQ.of(x));
    assertFalse(FiniteScalarQ.of(x));
  }

  @Test
  void testFinite2False() {
    Scalar x = CenteredInterval.of(DoubleScalar.POSITIVE_INFINITY, Pi.VALUE);
    assertFalse(ExactScalarQ.of(x));
    assertFalse(FiniteScalarQ.of(x));
  }

  @Test
  void testFinite3False() {
    Scalar x = CenteredInterval.of(Pi.VALUE, DoubleScalar.POSITIVE_INFINITY);
    assertFalse(ExactScalarQ.of(x));
    assertFalse(FiniteScalarQ.of(x));
  }

  @Test
  void testNumberThrows() {
    Scalar x = CenteredInterval.of(2, 1);
    assertThrows(Exception.class, () -> x.number());
  }

  @Test
  void testCompare() {
    assertEquals(Scalars.compare(CenteredInterval.of(3, 1), CenteredInterval.of(0, 1)), Integer.compare(2, 1));
    assertEquals(Scalars.compare(CenteredInterval.of(3, 1), CenteredInterval.of(10, 1)), Integer.compare(4, 5));
    assertThrows(Exception.class, () -> Scalars.compare(CenteredInterval.of(3, 1), CenteredInterval.of(4, 1)));
    assertThrows(Exception.class, () -> Scalars.compare(CenteredInterval.of(3, 1), CenteredInterval.of(2, 1)));
    assertThrows(Exception.class, () -> Scalars.compare(CenteredInterval.of(3, 1), CenteredInterval.of(3, 0.1)));
  }

  @Test
  void testCompareOther() {
    assertEquals(Scalars.compare(CenteredInterval.of(3, 1), RealScalar.of(1)), Integer.compare(2, 1));
    assertEquals(Scalars.compare(CenteredInterval.of(3, 1), RealScalar.of(5)), Integer.compare(4, 5));
    assertThrows(Exception.class, () -> Scalars.compare(CenteredInterval.of(3, 1), RealScalar.of(2)));
    assertThrows(Exception.class, () -> Scalars.compare(CenteredInterval.of(3, 1), RealScalar.of(4)));
    assertThrows(Exception.class, () -> Scalars.compare(CenteredInterval.of(3, 1), RealScalar.of(3)));
  }
}
