// code by jph
package ch.alpine.tensor.qty;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DurationScalarTest extends TestCase {
  public void testAddSubtract() throws ClassNotFoundException, IOException {
    DurationScalar d1 = DurationScalar.of(Duration.ofDays(213));
    DurationScalar d2 = DurationScalar.of(Duration.ofDays(113));
    DurationScalar d3 = DurationScalar.of(Duration.ofDays(100));
    Serialization.copy(d1);
    assertEquals(d1.subtract(d2), d3);
    assertEquals(d1.subtract(d3), d2);
    assertEquals(d2.add(d3), d1);
    assertEquals(d3.add(d2), d1);
    assertEquals(d1.divide(d3), RationalScalar.of(213, 100));
    assertEquals(d1.under(d3), RationalScalar.of(100, 213));
  }

  public void testAdd() {
    DateTimeScalar ofs = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    DurationScalar len = DurationScalar.of(Duration.ofDays(100));
    assertEquals(ofs.add(len), len.add(ofs));
    AssertFail.of(() -> ofs.add(RealScalar.TWO));
    AssertFail.of(() -> len.add(RealScalar.TWO));
    AssertFail.of(() -> len.compareTo(RealScalar.TWO));
    AssertFail.of(() -> len.number());
    AssertFail.of(() -> len.absSquared());
    AssertFail.of(() -> len.divide(ComplexScalar.I));
    AssertFail.of(() -> len.under(ComplexScalar.I));
  }

  public void testSubdivide() {
    DurationScalar d1 = DurationScalar.of(Duration.ofDays(213));
    DurationScalar d2 = DurationScalar.of(Duration.ofDays(113).negated());
    Subdivide.of(d1, d2, 41);
  }

  public void testMultiply() {
    DurationScalar d1 = DurationScalar.of(Duration.ofDays(100));
    DurationScalar d2 = d1.multiply(RealScalar.of(3));
    DurationScalar d3 = DurationScalar.of(Duration.ofDays(300));
    assertEquals(d2, d3);
    assertEquals(d2.hashCode(), d3.hashCode());
    assertEquals(d2.one(), RealScalar.ONE);
    assertEquals(d2.multiply(RealScalar.ONE), d2);
    assertEquals(RealScalar.ONE.multiply(d2), d2);
    AssertFail.of(() -> d2.reciprocal());
  }

  public void testDivideP() {
    DurationScalar d1 = DurationScalar.of(Duration.ofSeconds(100));
    Scalar d2 = d1.divide(RealScalar.of(3));
    DurationScalar d3 = DurationScalar.of(Duration.ofSeconds(33, 1_000_000_000 / 3));
    assertEquals(d2, d3);
  }

  public void testDivideN() {
    DurationScalar d1 = DurationScalar.of(Duration.ofSeconds(100).negated());
    Scalar d2 = d1.divide(RealScalar.of(3));
    DurationScalar d3 = DurationScalar.of(Duration.ofSeconds(33, 1_000_000_000 / 3 + 1).negated());
    assertEquals(d2, d3);
  }

  public void testUnderP() {
    DurationScalar d1 = DurationScalar.of(Duration.ofSeconds(100));
    AssertFail.of(() -> d1.under(RealScalar.of(300)));
  }

  public void testMultiplyRational() {
    DurationScalar d1 = DurationScalar.of(Duration.ofSeconds(100, 50));
    DurationScalar d2 = d1.multiply(RationalScalar.HALF);
    DurationScalar d3 = DurationScalar.of(Duration.ofSeconds(50, 25));
    assertEquals(d2, d3);
  }

  public void testAbs() {
    DurationScalar len = DurationScalar.of(Duration.ofDays(-100));
    Scalar scalar = Abs.FUNCTION.apply(len);
    assertEquals(scalar, DurationScalar.of(Duration.ofDays(+100)));
    assertEquals(Sign.FUNCTION.apply(len), RealScalar.ONE.negate());
    assertEquals(Sign.FUNCTION.apply(len.zero()), RealScalar.ZERO);
    assertEquals(scalar.multiply(RealScalar.ONE.negate()), len);
  }

  public void testToStringParse() {
    DurationScalar ds = DurationScalar.of(Duration.ofSeconds(245234, 123_236_987));
    String string = ds.toString();
    assertEquals(string, "PT68H7M14.123236987S");
    Scalar scalar = TemporalScalars.fromString(string);
    assertEquals(scalar, ds);
  }

  public void testNegateToStringParse() {
    DurationScalar ds = DurationScalar.of(Duration.ofSeconds(245234, 123_236_987).negated());
    String string = ds.toString();
    assertEquals(string, "PT-68H-7M-14.123236987S");
    Scalar scalar = TemporalScalars.fromString(string);
    assertEquals(scalar, ds);
  }

  public void testClip() {
    DurationScalar dt1 = DurationScalar.of(Duration.ofDays(-100));
    assertTrue(Sign.isNegative(dt1));
    DurationScalar dt2 = DurationScalar.of(Duration.ofDays(50));
    assertTrue(Sign.isPositive(dt2));
    assertEquals(Sign.FUNCTION.apply(dt2), RealScalar.ONE);
    Clip clip = Clips.interval(dt1, dt2);
    DurationScalar dt3 = DurationScalar.of(Duration.ofDays(20));
    clip.requireInside(dt3);
    DurationScalar dt4 = DurationScalar.of(Duration.ofDays(70));
    assertTrue(clip.isOutside(dt4));
  }

  public void testNEquals() {
    assertFalse(DurationScalar.of(Duration.ofSeconds(100)).equals(RationalScalar.HALF));
  }

  public void testExactScalar() {
    ExactScalarQ.require(DurationScalar.of(Duration.ofDays(-100)));
  }

  public void testNullFail() {
    AssertFail.of(() -> DurationScalar.of(null));
  }
}
