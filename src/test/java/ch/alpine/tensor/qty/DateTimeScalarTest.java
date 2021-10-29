// code by GRZ Technologies SA, jph
package ch.alpine.tensor.qty;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DateTimeScalarTest extends TestCase {
  public void test1() throws ClassNotFoundException, IOException {
    DateTimeScalar dt1 = new DateTimeScalar(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = new DateTimeScalar(LocalDateTime.of(2021, 1, 10, 6, 30));
    Serialization.copy(dt1);
    Scalar scalar1 = dt2.add(dt1.negate());
    assertTrue(scalar1 instanceof DurationScalar);
    Scalar scalar2 = dt2.subtract(dt1);
    assertTrue(scalar2 instanceof DurationScalar);
    assertEquals(scalar1, scalar2);
    assertEquals(dt1.add(scalar2), dt2);
    AssertFail.of(() -> dt1.negate().add(scalar2));
    assertEquals(dt2.add(dt1.multiply(RealScalar.of(-1))), scalar2);
    assertEquals(dt2.subtract(dt1.multiply(RealScalar.of(1))), scalar2);
  }

  @SuppressWarnings("unlikely-arg-type")
  public void testSpecific() {
    LocalDateTime ldt1 = LocalDateTime.of(2020, 12, 20, 4, 30);
    LocalDateTime ldt2 = LocalDateTime.of(2020, 12, 21, 4, 30);
    DateTimeScalar dt1 = new DateTimeScalar(ldt1);
    DateTimeScalar dt2 = new DateTimeScalar(ldt2);
    assertFalse(dt1.hashCode() == dt1.negate().hashCode());
    assertFalse(dt2.hashCode() == dt2.negate().hashCode());
    assertEquals(ldt1.compareTo(ldt2), Integer.compare(1, 2));
    assertEquals(ldt1.compareTo(ldt2), dt1.compareTo(dt2));
    Scalar oneDay = dt2.subtract(dt1);
    assertEquals(oneDay, DurationScalar.of(Duration.ofDays(1)));
    assertTrue(Sign.isPositive(oneDay));
    assertTrue(Sign.isPositiveOrZero(oneDay.zero()));
    assertTrue(Sign.isNegativeOrZero(oneDay.zero()));
    assertFalse(oneDay.equals("asd"));
  }

  public void test2() {
    DateTimeScalar dt1 = new DateTimeScalar(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = new DateTimeScalar(LocalDateTime.of(2021, 1, 10, 6, 30));
    Scalar scalar1 = dt2.add(dt1.negate());
    Scalar scalar3 = dt1.negate().add(dt2);
    assertTrue(scalar3 instanceof DurationScalar);
    assertEquals(scalar1, scalar3);
    scalar1.add(scalar3);
    Scalar diff = scalar1.subtract(scalar3);
    assertEquals(diff, diff.zero());
  }

  public void test3() {
    DateTimeScalar dt1 = new DateTimeScalar(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = new DateTimeScalar(LocalDateTime.of(2021, 1, 10, 6, 30));
    Scalar scalar1 = dt2.negate().add(dt1.negate().negate());
    Scalar scalar3 = dt1.negate().negate().add(dt2.negate());
    assertTrue(scalar3 instanceof DurationScalar);
    assertEquals(scalar1, scalar3);
    scalar1.add(scalar3);
    Scalar diff = scalar1.subtract(scalar3);
    assertEquals(diff, diff.zero());
  }

  public void testToStringParse() {
    DateTimeScalar dts = new DateTimeScalar(LocalDateTime.of(2020, 12, 20, 4, 30, 3, 125_239_876));
    String string = dts.toString();
    assertEquals(string, "2020-12-20T04:30:03.125239876");
    Scalar scalar = DateTimeScalar.fromString(string);
    assertEquals(scalar, dts);
  }

  public void testAddFail1() {
    DateTimeScalar dt1 = new DateTimeScalar(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = new DateTimeScalar(LocalDateTime.of(2020, 12, 21, 4, 30));
    assertFalse(dt1.equals(RealScalar.ONE));
    assertFalse(dt1.equals(dt1.negate()));
    assertFalse(dt1.equals(dt2));
    AssertFail.of(() -> dt1.add(dt2));
    AssertFail.of(() -> dt1.negate().add(dt2.negate()));
    AssertFail.of(() -> dt1.multiply(Pi.TWO));
    AssertFail.of(() -> dt1.reciprocal());
    AssertFail.of(() -> dt1.number());
    assertEquals(dt1.zero(), DurationScalar.ZERO);
    assertEquals(dt1.one(), RealScalar.ONE);
  }

  public void testAddFail2() {
    DateTimeScalar dt1 = new DateTimeScalar(LocalDateTime.of(2020, 12, 20, 4, 30));
    AssertFail.of(() -> dt1.add(RealScalar.of(3)));
    AssertFail.of(() -> dt1.add(ComplexScalar.I));
    AssertFail.of(() -> dt1.compareTo(ComplexScalar.I));
  }
}
