// code by jph
package ch.alpine.tensor.qty;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Abs;
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
    DateTimeScalar ofs = new DateTimeScalar(LocalDateTime.of(2020, 12, 20, 4, 30));
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

  public void testAbs() {
    DurationScalar len = DurationScalar.of(Duration.ofDays(-100));
    Scalar scalar = Abs.FUNCTION.apply(len);
    assertEquals(scalar, DurationScalar.of(Duration.ofDays(+100)));
  }

  public void testToStringParse() {
    DurationScalar ds = DurationScalar.of(Duration.ofSeconds(245234, 123_236_987));
    String string = ds.toString();
    assertEquals(string, "PT68H7M14.123236987S");
    Scalar scalar = DateTimeScalar.fromString(string);
    assertEquals(scalar, ds);
  }

  public void testNegateToStringParse() {
    DurationScalar ds = DurationScalar.of(Duration.ofSeconds(245234, 123_236_987).negated());
    String string = ds.toString();
    assertEquals(string, "PT-68H-7M-14.123236987S");
    Scalar scalar = DateTimeScalar.fromString(string);
    assertEquals(scalar, ds);
  }
}
