// code by jph
package ch.alpine.tensor.opt.fnd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.tmp.ResamplingMethod;
import ch.alpine.tensor.tmp.TimeSeries;

class FindRootTest {
  @Test
  void testJackpot() {
    FindRoot findRoot = FindRoot.of(x -> x, Chop._12);
    Chop._10.requireClose(findRoot.inside(Clips.interval(-1, 1)), RealScalar.ZERO);
    Chop._10.requireClose(findRoot.inside(Clips.interval(-1.0, 1.0)), RealScalar.ZERO);
  }

  @Test
  void testSimple() {
    FindRoot findRoot = FindRoot.of(Cos.FUNCTION, Chop._12);
    Chop._10.requireClose(findRoot.inside(Clips.interval(0.0, 4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.inside(Clips.interval(1.0, 4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.inside(Clips.interval(1.0, 2.0)), Pi.HALF);
    assertThrows(Throw.class, () -> findRoot.inside(Clips.interval(0.0, 1.0)));
  }

  @Test
  void testSimple2() {
    FindRoot findRoot = FindRoot.of(z -> Cos.FUNCTION.apply(z).negate(), Chop._12);
    Chop._10.requireClose(findRoot.inside(Clips.interval(0.0, 4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.inside(Clips.interval(1.0, 4.0)), Pi.HALF);
    Chop._10.requireClose(findRoot.inside(Clips.interval(1.0, 2.0)), Pi.HALF);
    assertThrows(Throw.class, () -> findRoot.inside(Clips.interval(0.0, 1.0)));
  }

  @Test
  void testInitZero() {
    FindRoot findRoot = FindRoot.of(z -> Sin.FUNCTION.apply(z).negate(), Chop._12);
    Chop._10.requireClose(findRoot.inside(Clips.interval(0.0, 4.0)), RealScalar.ZERO);
    Chop._10.requireClose(findRoot.inside(Clips.interval(RealScalar.of(0.5), Pi.VALUE)), Pi.VALUE);
    Chop._10.requireClose(findRoot.inside(Clips.interval(1.0, 4.0)), Pi.VALUE);
    assertThrows(Throw.class, () -> findRoot.inside(Clips.interval(1.0, 2.0)));
  }

  public static Scalar func(Scalar c) {
    Scalar T = RealScalar.of(300);
    Scalar r = RealScalar.of(700);
    Scalar t2 = Log.FUNCTION.apply(c.divide(RealScalar.ONE.subtract(c))).multiply(T);
    Scalar t1 = Times.of(c.subtract(Rational.HALF), RealScalar.of(4), r);
    return t1.subtract(t2);
  }

  @Test
  void testTs() {
    FindRoot findRoot = FindRoot.of(FindRootTest::func);
    Scalar x = findRoot.inside(Clips.interval(0.001, 0.499));
    Tolerance.CHOP.requireClose(x, RealScalar.of(0.01024075603728308));
    // System.out.println("1/2 -> " + func(RationalScalar.HALF));
    // System.out.println(x + " -> " + func(x));
    // System.out.println(x + " -> " + func(RealScalar.ONE.subtract(x)));
  }

  /** Function is equivalent to
   * <pre>
   * Polynomial.fit(Tensors.of(clip.min(), clip.max()), Tensors.of(y0, y1), 1).roots().Get(0);
   * </pre>
   * 
   * Functionality is implemented explicitly for speed.
   * 
   * @param clip
   * @param y0
   * @param y1
   * @return (x0 y1 - x1 y0) / (y1 - y0) */
  @PackageTestAccess
  static Scalar linear(Clip clip, Scalar y0, Scalar y1) {
    return LinearInterpolation.of(clip).apply(y0.divide(y0.subtract(y1)));
  }

  @Test
  void testLinear2() {
    Scalar scalar = linear(Clips.interval(10, 11), RealScalar.of(5), RealScalar.of(-2));
    assertEquals(scalar, Rational.of(75, 7));
  }

  @Test
  void testOther() {
    Scalar scalar = linear(Clips.interval(5, 6), RealScalar.of(2), RealScalar.of(-1));
    assertEquals(scalar, Rational.of(5 * 3 + 2, 3));
  }

  @Test
  void testFail() {
    FindRoot findRoot = FindRoot.of(s -> Sign.isPositiveOrZero(s) //
        ? RealScalar.ONE
        : RealScalar.ONE.negate());
    assertThrows(Exception.class, () -> findRoot.inside(Clips.absolute(1)));
  }

  @Test
  void testTimeSeriesLinear() {
    TimeSeries timeSeries = TimeSeries.path(Tensors.fromString("{{2, 4}, {5, -1}}"), ResamplingMethod.LINEAR_INTERPOLATION);
    FindRoot findRoot = FindRoot.of(x -> (Scalar) timeSeries.evaluate(x));
    Scalar scalar = findRoot.inside(timeSeries.domain());
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(4.4));
  }

  @RepeatedTest(10)
  void testTimeSeriesDateTime() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.LINEAR_INTERPOLATION);
    DateTime dateTime = DateTime.of(2020, 1, 1, 0, 0);
    timeSeries.insert(dateTime, RealScalar.of(-1));
    Scalar hi = RandomVariate.of(UniformDistribution.of(2, 3));
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), hi);
    FindRoot findRoot = FindRoot.of(x -> (Scalar) timeSeries.evaluate(x));
    Scalar scalar = findRoot.inside(timeSeries.domain());
    assertTrue(Tolerance.CHOP.allZero(timeSeries.evaluate(scalar)));
  }

  @Test
  void testTimeSeriesLoFail() {
    TimeSeries timeSeries = TimeSeries.path(Tensors.fromString("{{2, 4}, {5, -1}}"), ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    FindRoot findRoot = FindRoot.of(x -> (Scalar) timeSeries.evaluate(x));
    assertThrows(Exception.class, () -> findRoot.inside(timeSeries.domain()));
  }

  @Test
  void testTimeSeriesHiFail() {
    TimeSeries timeSeries = TimeSeries.path(Tensors.fromString("{{2, 4}, {5, -1}}"), ResamplingMethod.HOLD_VALUE_FROM_RIGHT);
    FindRoot findRoot = FindRoot.of(x -> (Scalar) timeSeries.evaluate(x));
    assertThrows(Exception.class, () -> findRoot.inside(timeSeries.domain()));
  }

  @Test
  void testAbove() {
    FindRoot findRoot = FindRoot.of(Cos.FUNCTION);
    assertTrue(findRoot.toString().startsWith("FindRoot["));
    Scalar x = findRoot.above(RealScalar.ZERO, RealScalar.ONE);
    Tolerance.CHOP.requireClose(x, Pi.HALF);
  }

  @Test
  void testAboveHit() {
    FindRoot findRoot = FindRoot.of(Sin.FUNCTION);
    assertTrue(findRoot.toString().startsWith("FindRoot["));
    Scalar x = findRoot.above(RealScalar.ZERO, RealScalar.ONE);
    assertEquals(x, RealScalar.ZERO);
  }

  @Test
  void testAboveFail() {
    FindRoot findRoot = FindRoot.of(_ -> RealScalar.ONE);
    assertThrows(Exception.class, () -> findRoot.above(RealScalar.ZERO, RealScalar.ONE));
  }
}
