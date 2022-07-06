// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

class DateTimeScalarTest {
  @Test
  void test1() throws ClassNotFoundException, IOException {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = DateTimeScalar.of(LocalDateTime.of(2021, 1, 10, 6, 30));
    Serialization.copy(dt1);
    Scalar scalar2 = dt2.subtract(dt1);
    assertInstanceOf(Quantity.class, scalar2);
    assertEquals(dt1.add(scalar2), dt2);
    assertThrows(Throw.class, () -> dt1.negate());
    assertThrows(Throw.class, () -> dt1.multiply(RealScalar.of(-1)));
    assertThrows(Throw.class, () -> dt1.subtract(RationalScalar.HALF));
    assertEquals(dt2.subtract(dt1.multiply(RealScalar.of(1))), scalar2);
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  void testSpecific() {
    LocalDateTime ldt1 = LocalDateTime.of(2020, 12, 20, 4, 30);
    LocalDateTime ldt2 = LocalDateTime.of(2020, 12, 21, 4, 30);
    DateTimeScalar dt1 = DateTimeScalar.of(ldt1);
    DateTimeScalar dt2 = DateTimeScalar.of(ldt2);
    assertEquals(ldt1.compareTo(ldt2), Integer.compare(1, 2));
    assertEquals(ldt1.compareTo(ldt2), dt1.compareTo(dt2));
    Scalar oneDay = dt2.subtract(dt1);
    // assertEquals(oneDay, DurationScalar.of(Duration.ofDays(1)));
    assertEquals(oneDay, Quantity.of(86400, "s"));
    assertTrue(Sign.isPositive(oneDay));
    assertTrue(Sign.isPositiveOrZero(oneDay.zero()));
    assertTrue(Sign.isNegativeOrZero(oneDay.zero()));
    assertFalse(oneDay.equals("asd"));
    assertFalse(dt1.hashCode() == dt2.hashCode());
  }

  @Test
  void testSubdivide() {
    LocalDateTime ldt1 = LocalDateTime.of(2020, 12, 20, 4, 30);
    LocalDateTime ldt2 = LocalDateTime.of(2020, 12, 21, 4, 30);
    DateTimeScalar dt1 = DateTimeScalar.of(ldt1);
    DateTimeScalar dt2 = DateTimeScalar.of(ldt2);
    Subdivide.of(dt1, dt2, 73);
  }

  @Test
  void test2() {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = DateTimeScalar.of(LocalDateTime.of(2021, 1, 10, 6, 30));
    Scalar scalar1 = dt2.subtract(dt1);
    Scalar scalar3 = dt1.subtract(dt2);
    assertInstanceOf(Quantity.class, scalar3);
    assertEquals(scalar1, scalar3.negate());
    scalar1.add(scalar3);
    Scalar diff = scalar1.add(scalar3);
    assertEquals(diff, diff.zero());
  }

  @Test
  void testToStringParse() {
    DateTimeScalar dts = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30, 3, 125_239_876));
    String string = dts.toString();
    assertEquals(string, "2020-12-20T04:30:03.125239876");
    Scalar scalar = TemporalScalars.fromString(string);
    assertEquals(scalar, dts);
  }

  @Test
  void testClip() {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2017, 12, 20, 4, 30));
    DateTimeScalar dt2 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 21, 4, 30));
    Clip clip = Clips.interval(dt1, dt2);
    DateTimeScalar dt3 = DateTimeScalar.of(LocalDateTime.of(2020, 9, 21, 0, 0, 0));
    clip.requireInside(dt3);
    DateTimeScalar dt4 = DateTimeScalar.of(LocalDateTime.of(2021, 1, 21, 0, 0, 0));
    assertTrue(clip.isOutside(dt4));
  }

  @Test
  void testExact() {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2017, 12, 20, 4, 30));
    ExactScalarQ.require(dt1);
    Scalar ds = Quantity.of(3, "days");
    Scalar scalar = dt1.subtract(ds);
    assertInstanceOf(DateTimeScalar.class, scalar);
    assertEquals(scalar.toString(), "2017-12-17T04:30");
  }

  @Test
  void testPiSeconds() {
    DateTimeScalar dateTimeScalar = DateTimeScalar.of(LocalDateTime.of(2013, 11, 30, 4, 54, 00));
    Scalar scalar = Quantity.of(Pi.in(30), "s");
    Scalar result = dateTimeScalar.add(scalar);
    assertEquals(result.toString(), "2013-11-30T04:54:03.141592653");
    Scalar swapin = scalar.add(dateTimeScalar);
    assertEquals(swapin.toString(), "2013-11-30T04:54:03.141592653");
  }

  @Test
  void testPiHours() {
    DateTimeScalar dateTimeScalar = DateTimeScalar.of(LocalDateTime.of(2013, 11, 30, 4, 54, 00));
    Scalar scalar = Quantity.of(Pi.in(30), "h");
    Scalar result = dateTimeScalar.add(scalar);
    assertEquals(result.toString(), "2013-11-30T08:02:29.733552923");
    Scalar swapin = scalar.add(dateTimeScalar);
    assertEquals(swapin.toString(), "2013-11-30T08:02:29.733552923");
  }

  @Test
  void testPiDays() {
    DateTimeScalar dateTimeScalar = DateTimeScalar.of(LocalDateTime.of(2013, 11, 30, 4, 54, 00));
    Scalar scalar = Quantity.of(Pi.in(30), "days");
    Scalar result = dateTimeScalar.add(scalar);
    assertEquals(result.toString(), "2013-12-03T08:17:53.605270158");
    Scalar swapin = scalar.add(dateTimeScalar);
    assertEquals(swapin.toString(), "2013-12-03T08:17:53.605270158");
  }

  @Test
  void testAddFail1() {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    DateTimeScalar dt2 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 21, 4, 30));
    assertFalse(dt1.equals(RealScalar.ONE));
    assertFalse(dt1.equals(dt2));
    assertEquals(dt1.compareTo(dt2), -1);
    assertEquals(dt2.compareTo(dt1), +1);
    assertThrows(Throw.class, () -> dt1.add(dt2));
    assertThrows(Throw.class, () -> dt1.negate().add(dt2.negate()));
    assertThrows(Throw.class, () -> dt1.multiply(Pi.TWO));
    assertThrows(Throw.class, () -> dt1.reciprocal());
    assertThrows(Throw.class, () -> dt1.number());
    assertEquals(dt1.zero(), Quantity.of(0, "s"));
    assertEquals(dt1.one(), RealScalar.ONE);
  }

  @Test
  void testAddFail2() {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    assertThrows(Throw.class, () -> dt1.add(RealScalar.of(3)));
    assertThrows(Throw.class, () -> dt1.add(ComplexScalar.I));
    assertThrows(Throw.class, () -> dt1.compareTo(ComplexScalar.I));
  }

  @Test
  void testAddSubtractFail1() {
    DateTimeScalar dt1 = DateTimeScalar.of(LocalDateTime.of(2020, 12, 20, 4, 30));
    assertThrows(Throw.class, () -> dt1.subtract(Pi.VALUE));
    assertThrows(Throw.class, () -> dt1.subtract(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> DateTimeScalar.of(null));
  }
}
