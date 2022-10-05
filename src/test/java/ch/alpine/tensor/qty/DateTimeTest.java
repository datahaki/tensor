// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;

class DateTimeTest {
  @Test
  void test1() throws ClassNotFoundException, IOException {
    Scalar dt1 = DateTime.of(2020, 12, 20, 4, 30);
    ExactScalarQ.require(dt1);
    Scalar dt2 = DateTime.of(2021, 1, 10, 6, 30);
    Serialization.copy(dt1);
    Scalar scalar2 = dt2.subtract(dt1);
    assertInstanceOf(Quantity.class, scalar2);
    assertEquals(dt1.add(scalar2), dt2);
    assertThrows(Throw.class, dt1::negate);
    assertThrows(Throw.class, () -> dt1.multiply(RealScalar.of(-1)));
    assertThrows(Throw.class, () -> dt1.subtract(RationalScalar.HALF));
    assertEquals(dt2.subtract(dt1.multiply(RealScalar.of(1))), scalar2);
    Clip clip = Clips.interval(dt1, dt2);
    DateTime dt3 = DateTime.of(2021, 1, 3, 2, 12);
    Scalar scalar = clip.rescale(dt3);
    ExactScalarQ.require(scalar);
    assertEquals(scalar, RationalScalar.of(3337, 5060));
    Scalar at = LinearInterpolation.of(clip).apply(scalar);
    assertEquals(at, dt3);
  }

  @Test
  void testSpecific() {
    Scalar dt1 = DateTime.of(2020, 12, 20, 4, 30);
    Scalar dt2 = DateTime.of(2020, 12, 21, 4, 30);
    assertEquals(Scalars.compare(dt1, dt2), Integer.compare(1, 2));
    assertEquals(Scalars.compare(dt1, dt1), Integer.compare(1, 1));
    Scalar oneDay = dt2.subtract(dt1);
    // assertEquals(oneDay, DurationScalar.of(Duration.ofDays(1)));
    assertEquals(oneDay, Quantity.of(86400, "s"));
    assertTrue(Sign.isPositive(oneDay));
    assertTrue(Sign.isPositiveOrZero(oneDay.zero()));
    assertTrue(Sign.isNegativeOrZero(oneDay.zero()));
    assertNotEquals("asd", oneDay);
    assertNotEquals(oneDay, "asd");
    assertNotEquals(dt1.hashCode(), dt2.hashCode());
  }

  @Test
  void testSubdivide() {
    Scalar dt1 = DateTime.of(2020, 12, 20, 4, 30);
    Scalar dt2 = DateTime.of(2020, 12, 21, 4, 30);
    Subdivide.of(dt1, dt2, 73);
  }

  @Test
  void testDateAndTime() {
    DateTime dt1 = DateTime.of(2020, 12, 20, 4, 30, 23, 234_345_123);
    DateTime dt2 = DateTime.of( //
        dt1.localDateTime().toLocalDate(), //
        dt1.localDateTime().toLocalTime());
    assertEquals(dt1, dt2);
  }

  @Test
  void testPlus() {
    Scalar dt1 = DateTime.now();
    assertThrows(Exception.class, () -> dt1.add(Quantity.of(3, "m")));
    Scalar delta = Quantity.of(3, "s");
    Scalar res = dt1.add(delta);
    assertEquals(res.one(), RealScalar.ONE);
    Scalar re2 = delta.add(dt1);
    assertEquals(res, re2);
  }

  @Test
  void test2() {
    Scalar dt1 = DateTime.of(2020, 12, 20, 4, 30);
    Scalar dt2 = DateTime.of(2021, 1, 10, 6, 30);
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
    Scalar dts = DateTime.of(2020, 12, 20, 4, 30, 3, 125_239_876);
    String string = dts.toString();
    assertEquals(string, "2020-12-20T04:30:03.125239876");
    Scalar scalar = DateTime.parse(string);
    assertEquals(scalar, dts);
    assertThrows(DateTimeParseException.class, () -> DateTime.parse("2020-12-20"));
  }

  @Test
  void testPlusDays() {
    Scalar dts = DateTime.parse("2004-02-28T00:00").plusDays(1);
    String string = dts.toString();
    assertEquals(string, "2004-02-29T00:00");
    Scalar scalar = DateTime.parse(string);
    assertEquals(scalar, dts);
  }

  @Test
  void testNowNanos() {
    LocalDateTime localDateTime = LocalDateTime.now();
    assertDoesNotThrow(localDateTime::toLocalTime);
  }

  @Test
  void testClip() {
    DateTime dt1 = DateTime.of(2017, 12, 20, 4, 30);
    assertEquals(dt1.year(), 2017);
    assertEquals(dt1.month(), Month.DECEMBER);
    assertEquals(dt1.dayOfMonth(), 20);
    DateTime dt2 = DateTime.of(2020, 12, 21, 4, 30);
    Clip clip = Clips.interval(dt1, dt2);
    DateTime dt3 = DateTime.of(2020, 9, 21, 0, 0, 0);
    clip.requireInside(dt3);
    DateTime dt4 = DateTime.of(2021, 1, 21, 0, 0, 0);
    assertTrue(clip.isOutside(dt4));
  }

  @Test
  void testClip2() {
    int year = 2022;
    Scalar ldtStart = DateTime.of(year, 1, 1, 0, 0);
    Scalar ldtEnd = DateTime.of(year, 12, 31, 0, 0);
    Clip clip = Clips.interval(ldtStart, ldtEnd);
    assertEquals(clip.width(), Quantity.of(31449600, "s"));
    Clips.positive(ldtEnd.subtract(ldtStart)).requireInside(Quantity.of(234445, "s"));
  }

  @Test
  void testPlusYears() {
    DateTime dt1 = DateTime.of(2004, 2, 29, 0, 0);
    assertEquals(dt1.dayOfWeek(), DayOfWeek.SUNDAY);
    DateTime dt2 = dt1.plusYears(1);
    assertEquals(dt2.toString(), "2005-02-28T00:00");
    assertTrue(FiniteScalarQ.of(dt1));
  }

  @Test
  void testPlusMonths() {
    DateTime dt1 = DateTime.of(2004, 1, 31, 0, 0);
    DateTime dt2 = dt1.plusMonths(1);
    assertEquals(dt2.toString(), "2004-02-29T00:00");
  }

  @Test
  void testPlusHours() {
    Scalar dt1 = DateTime.of(2004, 2, 28, 0, 0);
    Scalar dt2 = dt1.add(Quantity.of(24 + 25, "h"));
    assertEquals(dt2.toString(), "2004-03-01T01:00");
  }

  @Test
  void testExact() {
    Scalar dt1 = DateTime.of(2017, 12, 20, 4, 30);
    ExactScalarQ.require(dt1);
    Scalar ds = Quantity.of(3, "days");
    Scalar scalar = dt1.subtract(ds);
    assertInstanceOf(DateTime.class, scalar);
    assertEquals(scalar.toString(), "2017-12-17T04:30");
  }

  @Test
  void testPiSeconds() {
    Scalar dateTime = DateTime.of(2013, 11, 30, 4, 54, 0);
    Scalar scalar = Quantity.of(Pi.in(30), "s");
    Scalar result = dateTime.add(scalar);
    assertEquals(result.toString(), "2013-11-30T04:54:03.141592653");
    Scalar swapin = scalar.add(dateTime);
    assertEquals(swapin.toString(), "2013-11-30T04:54:03.141592653");
  }

  @Test
  void testDTSHi() {
    DateTime dts1 = DateTime.of(2013, 11, 30, 4, 54, 0, 123_213_678);
    Scalar scalar1 = dts1.floor().toEpoch(ZoneOffset.MIN);
    Scalar scalar2 = dts1.toEpoch(ZoneOffset.MIN);
    Scalar scalar3 = Floor.FUNCTION.apply(scalar2);
    assertEquals(scalar1, scalar3);
    Sign.requirePositive(scalar1);
    DateTime dts2 = DateTime.ofEpoch(scalar2, ZoneOffset.MIN);
    assertEquals(dts1, dts2);
  }

  @Test
  void testDTSLo() {
    DateTime dts1 = DateTime.of(1965, 11, 30, 4, 54, 0, 123_213_678);
    Scalar scalar1 = dts1.floor().toEpoch(ZoneOffset.MAX);
    Scalar scalar2 = dts1.toEpoch(ZoneOffset.MAX);
    Scalar scalar3 = Floor.FUNCTION.apply(scalar2);
    assertEquals(scalar1, scalar3);
    Sign.requirePositive(scalar1.negate());
    DateTime dts2 = DateTime.ofEpoch(scalar2, ZoneOffset.MAX);
    assertEquals(dts1, dts2);
  }

  @Test
  void testPiHours() {
    DateTime dateTime = DateTime.of(2013, 11, 30, 4, 54, 0);
    LocalDateTime localDateTime = dateTime.localDateTime();
    assertEquals(localDateTime, LocalDateTime.of(2013, 11, 30, 4, 54, 0));
    Scalar scalar = Quantity.of(Pi.in(30), "h");
    Scalar result = dateTime.add(scalar);
    assertEquals(result.toString(), "2013-11-30T08:02:29.733552923");
    Scalar swapin = scalar.add(dateTime);
    assertEquals(swapin.toString(), "2013-11-30T08:02:29.733552923");
  }

  @Test
  void testPiDays() {
    DateTime dateTime = DateTime.of(2013, 11, 30, 4, 54, 0);
    Scalar scalar = Quantity.of(Pi.in(30), "days");
    Scalar result = dateTime.add(scalar);
    assertEquals(result.toString(), "2013-12-03T08:17:53.605270158");
    Scalar swapin = scalar.add(dateTime);
    assertEquals(swapin.toString(), "2013-12-03T08:17:53.605270158");
  }

  @Test
  void testAdd() {
    final Scalar delta = Quantity.of(3, "s");
    DateTime dateTime1 = DateTime.of(2000, 1, 30, 4, 54, 0);
    Scalar scalar1 = dateTime1.add(delta);
    DateTime dateTime2 = DateTime.of(2000, 1, 30, 4, 54, 3);
    assertEquals(scalar1, dateTime2);
    Scalar scalar2 = dateTime2.subtract(delta);
    assertEquals(scalar2, dateTime1);
    Scalar scalar3 = delta.add(dateTime1);
    assertEquals(scalar3, dateTime2);
  }

  @Test
  void testStaticConstructors() {
    assertEquals(DateTime.of(2000, 1, 30, 4, 54), DateTime.of(2000, Month.JANUARY, 30, 4, 54));
    assertEquals(DateTime.of(2001, 2, 28, 4, 54, 6), DateTime.of(2001, Month.FEBRUARY, 28, 4, 54, 6));
    assertEquals(DateTime.of(2002, 3, 31, 4, 54, 6, 123), DateTime.of(2002, Month.MARCH, 31, 4, 54, 6, 123));
  }

  @Test
  void testAddFail1() {
    DateTime dt1 = DateTime.of(2020, 12, 20, 4, 30);
    DateTime dt2 = DateTime.of(2020, 12, 21, 4, 30);
    assertFalse(dt1.equals(RealScalar.ONE));
    assertFalse(dt1.equals(dt2));
    assertEquals(dt1.compareTo(dt2), -1);
    assertEquals(dt2.compareTo(dt1), +1);
    assertThrows(Throw.class, () -> dt1.add(dt2));
    assertThrows(Throw.class, () -> dt1.negate().add(dt2.negate()));
    assertThrows(Throw.class, () -> dt1.multiply(Pi.TWO));
    assertThrows(Throw.class, dt1::reciprocal);
    assertThrows(Throw.class, dt1::number);
    assertEquals(dt1.zero(), Quantity.of(0, "s"));
    assertEquals(dt1.one(), RealScalar.ONE);
  }

  @Test
  void testAddFail2() {
    DateTime dt1 = DateTime.of(2020, 12, 20, 4, 30);
    assertEquals(dt1, dt1.floor());
    assertNotEquals(dt1, "asd");
    assertThrows(Throw.class, () -> dt1.add(RealScalar.of(3)));
    assertThrows(Throw.class, () -> dt1.add(ComplexScalar.I));
    assertThrows(Throw.class, () -> dt1.compareTo(ComplexScalar.I));
  }

  @Test
  void testAddSubtractFail1() {
    DateTime dt1 = DateTime.of(2020, 12, 20, 4, 30);
    assertThrows(Throw.class, () -> dt1.subtract(Pi.VALUE));
    assertThrows(Throw.class, () -> dt1.subtract(Tensors.vector(1, 2, 3)));
  }

  @Test
  void testNow() {
    assertTrue(DateTime.now().toString().compareTo("2022") > 0);
  }

  @Test
  void testFromTo() {
    String input = "2020-02-29T02:33:09.530";
    DateTime ldt1 = DateTime.parse(input);
    assertEquals(ldt1.toString(), input);
    DateTime ldt2 = ldt1.plusYears(1);
    Scalar fromTo = ldt2.subtract(ldt1);
    assertEquals(fromTo, Quantity.of(3.1536E7, "s"));
  }

  @Test
  void testWithoutNanos() {
    DateTime now = DateTime.now();
    DateTime trc = now.floor();
    assertTrue(now.toString().startsWith(trc.toString()));
    assertTrue(trc.toString().length() <= 19); // may also be 16 if seconds part == 0
  }

  @Test
  void testSimple1() {
    Tensor a = Tensors.of( //
        DateTime.of(1657, 11, 10, 4, 8), //
        DateTime.of(1857, 10, 5, 7, 18), //
        RationalScalar.HALF);
    Tensor b = Tensors.of( //
        DateTime.of(2021, 7, 3, 14, 48), //
        DateTime.of(1976, 4, 1, 17, 28), //
        RealScalar.TWO);
    Tensor diff = a.subtract(b);
    Tensor recv = Tensors.fromString(diff.toString());
    assertEquals(diff, recv);
    assertEquals(b.add(recv), a);
    assertEquals(recv.add(b), a);
    assertEquals(a.subtract(recv), b);
    assertEquals(recv.negate().add(a), b);
    ExactTensorQ.require(diff);
    assertEquals(Sign.of(b.subtract(a)), Tensors.vector(+1, +1, +1));
    assertEquals(Sign.of(a.subtract(b)), Tensors.vector(-1, -1, -1));
  }

  @Test
  void testParsing2() {
    Tensor a = Tensors.of( //
        DateTime.of(1657, 11, 10, 4, 8), //
        DateTime.of(1857, 10, 5, 7, 18));
    Tensor recv = Tensors.fromString(a.toString(), DateTime::parse);
    assertEquals(a, recv);
    ExactTensorQ.require(a);
  }

  @Test
  void testParse3() {
    assertThrows(Exception.class, () -> DateTime.parse("2022-09-20"));
  }

  @Test
  void testDuration4() {
    Scalar scalar = DateTime.seconds(Duration.ofDays(3));
    Duration duration = DateTime.duration(scalar);
    assertEquals(duration, Duration.ofDays(3));
  }

  @Test
  void testNow2() {
    DateTime dateTime1 = DateTime.now();
    LocalDateTime localDateTime = dateTime1.localDateTime();
    DateTime dateTime2 = DateTime.of(localDateTime);
    assertEquals(dateTime1, dateTime2);
    assertEquals(dateTime1.subtract(dateTime2), Quantity.of(0, "s"));
  }

  @Test
  void testMillis() {
    // Switzerland has +2 hours
    long millis = System.currentTimeMillis();
    ZoneOffset zoneOffset = ZoneOffset.ofHours(2);
    DateTime dateTime = DateTime.ofEpoch(Quantity.of(millis, "ms"), zoneOffset);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    dateTime.format(dateTimeFormatter);
    dateTime.floor();
  }

  private static final ScalarUnaryOperator TO_MILLIS = QuantityMagnitude.SI().in("ms");

  /** @param zoneOffset
   * @return date instance */
  public static Date toDate(DateTime dateTime, ZoneOffset zoneOffset) {
    return new Date(Floor.longValueExact(TO_MILLIS.apply(dateTime.toEpoch(zoneOffset))));
  }

  @Test
  void testDate() {
    // Switzerland has +2 hours
    long millis = System.currentTimeMillis();
    ZoneOffset zoneOffset = ZoneOffset.ofHours(2);
    DateTime dateTime = DateTime.ofEpoch(Quantity.of(millis, "ms"), zoneOffset);
    Date date = toDate(dateTime, zoneOffset);
    assertNotNull(date);
  }

  @Test
  void testRoundingDown() {
    DateTime dateTime = DateTime.of(1982, Month.APRIL, 3, 23, 59, 59, 499_999_999);
    DateTime lo = DateTime.of(1982, Month.APRIL, 3, 23, 59, 59);
    DateTime hi = DateTime.of(1982, Month.APRIL, 4, 0, 0);
    assertEquals(Ceiling.FUNCTION.apply(dateTime), hi);
    assertEquals(Floor.FUNCTION.apply(dateTime), lo);
    assertEquals(Round.FUNCTION.apply(dateTime), lo);
  }

  @Test
  void testRoundingUp() {
    DateTime dateTime = DateTime.of(1982, Month.APRIL, 3, 23, 59, 59, 500_000_000);
    DateTime lo = DateTime.of(1982, Month.APRIL, 3, 23, 59, 59);
    DateTime hi = DateTime.of(1982, Month.APRIL, 4, 0, 0);
    assertEquals(Ceiling.FUNCTION.apply(dateTime), hi);
    assertEquals(Floor.FUNCTION.apply(dateTime), lo);
    assertEquals(Round.FUNCTION.apply(dateTime), hi);
  }

  @Test
  void testRoundingInvariant() {
    DateTime dateTime = DateTime.of(1982, Month.APRIL, 3, 23, 59, 59, 0);
    DateTime lo = DateTime.of(1982, Month.APRIL, 3, 23, 59, 59);
    assertEquals(Ceiling.FUNCTION.apply(dateTime), lo);
    assertEquals(Floor.FUNCTION.apply(dateTime), lo);
    assertEquals(Round.FUNCTION.apply(dateTime), lo);
  }

  @Test
  void testZeroAndOneSame() {
    assertSame(DateTime.now().zero(), DateTime.now().zero());
    assertSame(DateTime.now().one(), DateTime.now().one());
  }

  @Test
  void testLargeDurationFail() {
    Scalar scalar = Quantity.of(new BigInteger("25782639457862394578623945786294578629378456"), "s");
    assertThrows(ArithmeticException.class, () -> DateTime.duration(scalar));
  }

  @Test
  void testDateTimeIndeterminate() {
    assertThrows(Exception.class, () -> DateTime.now().multiply(DoubleScalar.INDETERMINATE));
  }

  @Test
  void testFormat() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS");
    String string = DateTime.of(1998, Month.FEBRUARY, 3, 14, 48, 38, 234_876_128).format(dateTimeFormatter);
    assertEquals(string.length(), 29);
  }

  @Test
  void testQuantityFail() {
    DateTime dateTime = DateTime.now();
    {
      Scalar dom = Quantity.of(dateTime, "m");
      Scalar dor = Quantity.of(dateTime.plus(Quantity.of(3, "s")), "r");
      assertThrows(Exception.class, () -> dom.add(dor));
    }
    {
      Scalar dom = Quantity.of(dateTime, "m");
      Scalar dor = Quantity.of(3, "m");
      assertThrows(Exception.class, () -> dom.add(dor));
    }
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> DateTime.of(null));
  }
}
