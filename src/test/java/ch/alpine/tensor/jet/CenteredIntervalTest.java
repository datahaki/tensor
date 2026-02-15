// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

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
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.DateTime;
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
  void testNegate() throws ClassNotFoundException, IOException {
    Scalar scalar = Serialization.copy(CenteredInterval.of(6, 1)).negate();
    assertEquals(scalar, CenteredInterval.of(-6, 1));
  }

  @Test
  void testPlus() {
    CenteredInterval x = (CenteredInterval) CenteredInterval.of(20, 1);
    Scalar y = CenteredInterval.of(10, 2);
    assertEquals(x.add(y), CenteredInterval.of(30, 3));
    assertEquals(x.center(), RealScalar.of(20));
    assertEquals(x.radius(), RealScalar.of(1));
  }

  @Test
  void testPlus1() {
    Scalar x = CenteredInterval.of(20, 1);
    Scalar y = Pi.VALUE;
    assertEquals(x.add(y), CenteredInterval.of(RealScalar.of(20).add(Pi.VALUE), RealScalar.of(1)));
  }

  @Test
  void testMultiply() {
    Scalar x = CenteredInterval.of(20, 1);
    Scalar y = CenteredInterval.of(10, 2);
    assertEquals(x.multiply(y), CenteredInterval.of(202, 50)); // 200 +- 52
  }

  @Test
  void testMultiply2() {
    Scalar x = CenteredInterval.of(20, 1);
    Scalar y = RealScalar.of(-3);
    assertEquals(x.multiply(y), CenteredInterval.of(-60, 3));
    assertEquals(x.multiply(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testInverse1() {
    Scalar ci = CenteredInterval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)));
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), ci), //
        Tensors.of(RealScalar.of(2), RealScalar.of(4)));
    Sign.requirePositive(Det.of(matrix));
    Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
  }

  @Test
  void testInverse2() {
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(3), CenteredInterval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))), //
        Tensors.of(RealScalar.of(2), CenteredInterval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)))));
    Sign.requirePositive(Det.of(matrix));
    Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
  }

  @Test
  void testInverse3() {
    Scalar ci = CenteredInterval.of(Clips.interval(RationalScalar.of(9, 10), RationalScalar.of(11, 10)));
    Tensor matrix = Tensors.of( //
        Tensors.of(RealScalar.of(1), ci), //
        Tensors.of(RealScalar.of(0), RealScalar.of(1)));
    Sign.requirePositive(Det.of(matrix));
    Tensor tensor = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    Tensor result = Inverse.of(tensor);
    assertEquals(matrix, result);
  }

  @Test
  void testAbs() {
    assertEquals(Abs.FUNCTION.apply(CenteredInterval.of(-4, 1)), CenteredInterval.of(Clips.interval(3, 5)));
    assertEquals(Abs.FUNCTION.apply(CenteredInterval.of(-4, 2)), CenteredInterval.of(Clips.interval(2, 6)));
  }

  @Test
  void testRadiusNegFail() {
    assertThrows(Exception.class, () -> CenteredInterval.of(+4, -2));
    assertThrows(Exception.class, () -> CenteredInterval.of(-4, -2));
  }

  @Test
  void testAbsSquared() {
    assertEquals(AbsSquared.FUNCTION.apply(CenteredInterval.of(-4, 1)), CenteredInterval.of(17, 8));
    assertEquals(AbsSquared.FUNCTION.apply(CenteredInterval.of(-4, 2)), CenteredInterval.of(20, 16));
    assertEquals(AbsSquared.FUNCTION.apply(CenteredInterval.of(-1, 2)), CenteredInterval.of(Clips.positive(9)));
  }

  @Test
  void testExact() {
    assertFalse(ExactScalarQ.of(CenteredInterval.of(-4, 1)));
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
    assertEquals(Sign.FUNCTION.apply(CenteredInterval.of(2.3, 5.6)), CenteredInterval.of(0, 1));
    assertEquals(Sign.FUNCTION.apply(CenteredInterval.of(2.3, 0.6)), CenteredInterval.of(1, 0));
    assertEquals(Sign.FUNCTION.apply(CenteredInterval.of(-.3, 0.1)), CenteredInterval.of(-1, 0));
  }

  @Test
  void testReciprocalP() {
    Scalar scalar = CenteredInterval.of(5, 1);
    scalar.reciprocal().toString();
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
  void testExp() {
    Exp.FUNCTION.apply(CenteredInterval.of(-4, 1));
    CenteredInterval.of(-4, 1).hashCode();
    assertThrows(Exception.class, () -> Log.FUNCTION.apply(CenteredInterval.of(2, 3)));
  }

  @Test
  void testPower() {
    Power.of(CenteredInterval.of(-4, 1), 7);
  }

  @Test
  void test() {
    Scalar x = CenteredInterval.of(10, 2);
    Scalar y = CenteredInterval.of(-8, 7);
    assertNotEquals(x, y);
    assertNotEquals(x.hashCode(), y.hashCode());
    assertEquals(x.add(y), CenteredInterval.of(2, 9));
    assertEquals(x.negate(), CenteredInterval.of(-10, 2));
    assertFalse(ExactScalarQ.of(x));
  }

  @Test
  void testAbs1() {
    Scalar x = CenteredInterval.of(3, 4);
    Scalar y = CenteredInterval.of(3.5, 3.5);
    assertEquals(Abs.FUNCTION.apply(x), y);
  }

  @Test
  void testZeroRadius() {
    assertEquals(CenteredInterval.of(3, 0), RealScalar.of(3));
    assertEquals(CenteredInterval.of(Clips.interval(4, 4)), RealScalar.of(4));
  }

  @Test
  void testAbs2() {
    Scalar x = CenteredInterval.of(7, 4);
    assertEquals(Abs.FUNCTION.apply(x), x);
    assertNotEquals(x, x.zero());
    Scalar y = AbsSquared.FUNCTION.apply(x);
    assertEquals(y, CenteredInterval.of(65, 56));
  }

  @Test
  void testExp2() {
    Scalar x = CenteredInterval.of(2, 1);
    CenteredInterval exp = (CenteredInterval) Exp.FUNCTION.apply(x);
    assertEquals(exp.clip(), Clips.interval(Math.exp(1), Math.exp(3)));
  }

  @Test
  void testLog() {
    Scalar x = CenteredInterval.of(3, 1);
    CenteredInterval exp = (CenteredInterval) Log.FUNCTION.apply(x);
    assertEquals(exp.clip(), Clips.interval(Math.log(2), Math.log(4)));
  }

  @Test
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
    assertThrows(Exception.class, x::number);
  }

  @Test
  void testConstructorThrows() {
    assertThrows(Exception.class, () -> CenteredInterval.of(Quantity.of(3, "s"), Quantity.of(0, "m")));
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
