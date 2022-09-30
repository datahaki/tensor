// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.RoundingInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.LaplaceDistribution;
import ch.alpine.tensor.pdf.c.LogisticDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Floor;

/** DateTime is a wrapper of {@link LocalDateTime}, i.e. encodes the same
 * information as {@link LocalDateTime}, namely a date such as 2022-09-27
 * and a time such as 12:34:56.123456789 without specification of a
 * time-zone, {@link ZoneOffset}.
 * 
 * The purpose of {@link DateTime} is to allow the interoperation
 * between {@link LocalDateTime} and {@link Quantity} with temporal SI-unit,
 * for instance "s", "ms", "h", etc.
 * 
 * The difference between two {@link DateTime}s is a {@link Quantity} with
 * unit "s" and value as a rational scalar, i.e. in exact precision.
 * 
 * In particular, that makes the use of type {@link Duration} obsolete.
 * 
 * An {@link DateTime} encodes an absolute point in a local calendar by
 * wrapping an instance of {@link LocalDateTime}.
 * 
 * Addition and subtraction of a quantity with temporal SI unit is supported.
 * This includes the units "s", "ms", "h", "days", ...
 * The result of the sum is a new instance of {@link DateTime}.
 * 
 * Addition of two instances of {@link DateTime} results in an exception.
 * Subtraction of two instances of {@link DateTime} results in a {@link Quantity}.
 * Negation of a {@link DateTime} results in an exception.
 * 
 * The string expression is of a date time scalar adheres to the ISO-8601
 * formats, i.e. the pattern uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS
 * resulting in strings of the form
 * 2022-09-16T10:42:13.724124032
 * but with seconds and nanos zero can be as truncated as
 * 2022-09-16T10:42
 * 
 * {@link LocalDateTime} offers methods to perform calendar arithmetic such as
 * increasing the year, or month. Equivalent methods are available.
 * 
 * {@link LocalDateTime} offers getter methods for year, month, dayOfMonth.
 * Equivalent methods are available.
 * 
 * {@link RoundingInterface} rounds to the nearest seconds, which is decided
 * based on the nano part of the {@link DateTime}. A nano part equal or
 * above 500_000_000 is rounded up, otherwise down.
 * 
 * Several continuous distributions accept DateTime as input parameter:
 * {@link NormalDistribution}, {@link UniformDistribution},
 * {@link TrapezoidalDistribution}, {@link LaplaceDistribution},
 * {@link CauchyDistribution}, {@link LogisticDistribution}, etc.
 * 
 * Remark:
 * A similar functionality is provided by Mathematica as
 * <a href="https://reference.wolfram.com/language/ref/DateObject.html">DateObject</a>.
 * 
 * However, we deviate from the name, because DateObject allows a single year
 * DateObject[{2000}] == Year: 2000
 * whereas DateTime/LocalDateTime requires everything from year down to a minute,
 * after which seconds and nano-seconds are assumed to be zero.
 * Also, the term "Object" does not add meaning in Java.
 * 
 * @see FiniteScalarQ
 * @see ExactScalarQ
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class DateTime extends AbstractScalar implements //
    Comparable<Scalar>, QuantityCompatibleScalar, RoundingInterface, Serializable {
  private static final long NANOS_LONG = 1_000_000_000;
  private static final Scalar NANOS = RealScalar.of(NANOS_LONG);
  private static final Unit UNIT_S = Unit.of("s");
  private static final ScalarUnaryOperator MAGNITUDE_S = QuantityMagnitude.SI().in(UNIT_S);
  private static final Scalar ZERO = Quantity.of(RealScalar.ZERO, UNIT_S);

  /** @param localDateTime non-null
   * @return
   * @throws Exception if given localDateTime is null */
  public static DateTime of(LocalDateTime localDateTime) {
    return new DateTime(Objects.requireNonNull(localDateTime));
  }

  /** @param localDate non-null
   * @param localTime non-null
   * @return
   * @throws Exception if either parameter is null */
  public static DateTime of(LocalDate localDate, LocalTime localTime) {
    return new DateTime(LocalDateTime.of(localDate, localTime));
  }

  public static DateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
    return new DateTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute));
  }

  public static DateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
    return new DateTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second));
  }

  public static DateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
    return new DateTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond));
  }

  public static DateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
    return new DateTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute));
  }

  public static DateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
    return new DateTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second));
  }

  public static DateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
    return new DateTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond));
  }

  /** @return instance based on LocalDateTime.now() */
  public static DateTime now() {
    return new DateTime(LocalDateTime.now());
  }

  /** @param scalar with time based SI-unit, for instance "s", "h", "days", "wk", "mo", "yr", etc.
   * @param zoneOffset
   * @return epoch of 1970-01-01T00:00:00Z plus the duration specified in given scalar relative to
   * given zoneOffset */
  public static DateTime ofEpoch(Scalar scalar, ZoneOffset zoneOffset) {
    Scalar seconds = MAGNITUDE_S.apply(scalar);
    Scalar floor = Floor.FUNCTION.apply(seconds);
    Scalar nanos = seconds.subtract(floor).multiply(NANOS);
    return new DateTime(LocalDateTime.ofEpochSecond( //
        floor.number().longValue(), //
        nanos.number().intValue(), //
        zoneOffset));
  }

  /** parsing function
   * 
   * Examples:
   * 2020-12-20T04:30:03.125239876 parses to {@link DateTime}
   * 
   * @param string
   * @return
   * @throws java.time.format.DateTimeParseException if {@code string} cannot be parsed
   * @see LocalDateTime#parse(CharSequence) */
  public static DateTime parse(String string) {
    return new DateTime(LocalDateTime.parse(string));
  }

  /** @param duration
   * @return quantity with value equals to the number of seconds encoded in given duration and unit "s" */
  public static Scalar seconds(Duration duration) {
    Scalar seconds = RealScalar.of(duration.getSeconds());
    Scalar faction = RationalScalar.of(duration.getNano(), NANOS_LONG);
    return Quantity.of(seconds.add(faction), UNIT_S);
  }

  /** @param scalar with unit "s" or compatible
   * @return */
  public static Duration duration(Scalar scalar) {
    scalar = MAGNITUDE_S.apply(scalar);
    Scalar integral = Floor.FUNCTION.apply(scalar);
    return Duration.ofSeconds( //
        Scalars.longValueExact(integral), //
        scalar.subtract(integral).multiply(NANOS).number().longValue());
  }

  // ---
  private final LocalDateTime localDateTime;

  private DateTime(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }

  /** @return instance of {@link LocalDateTime} backing this {@link DateTime} */
  public LocalDateTime localDateTime() {
    return localDateTime;
  }

  /** @param zoneOffset
   * @return seconds from the epoch of 1970-01-01T00:00:00Z with unit "s"
   * which is negative for dates before that threshold in exact precision */
  public Scalar toEpoch(ZoneOffset zoneOffset) {
    // function getNano() returns in the range 0 to 999,999,999
    Scalar nanos = RationalScalar.of(localDateTime.getNano(), NANOS_LONG);
    return Quantity.of(RealScalar.of(localDateTime.toEpochSecond(zoneOffset)).add(nanos), UNIT_S);
  }

  /** @return the year, from MIN_YEAR to MAX_YEAR */
  public int year() {
    return localDateTime.getYear();
  }

  /** @return the month-of-year, not null */
  public Month month() {
    return localDateTime.getMonth();
  }

  /** @return the day-of-month, from 1 to 31 */
  public int dayOfMonth() {
    return localDateTime.getDayOfMonth();
  }

  public DayOfWeek dayOfWeek() {
    return localDateTime.getDayOfWeek();
  }

  /** Example:
   * 2004-02-29T00:00 plus 1 year == 2005-02-28T00:00
   * 
   * @param years to add, may be negative
   * @return this date-time with the years added, not null
   * @throws DateTimeException if the result exceeds the supported date range */
  public DateTime plusYears(int years) {
    return new DateTime(localDateTime.plusYears(years));
  }

  /** Example:
   * 2004-01-31T00:00 plus 1 month == 2004-02-29T00:00
   * 
   * @param months to add, may be negative
   * @return this date-time with the months added, not null
   * @throws DateTimeException if the result exceeds the supported date range */
  public DateTime plusMonths(long months) {
    return new DateTime(localDateTime.plusMonths(months));
  }

  /** Example:
   * 2004-02-28T00:00 plus 1 day == 2004-02-29T00:00
   * 
   * @param days to add, may be negative
   * @return this date-time with the days added, not null
   * @throws DateTimeException if the result exceeds the supported date range */
  public DateTime plusDays(long days) {
    return new DateTime(localDateTime.plusDays(days));
  }

  @Override // from RoundingInterface
  public DateTime ceiling() {
    return 0 == localDateTime.getNano() //
        ? this
        : new DateTime(localDateTime.withNano(0).plusSeconds(1));
  }

  /** @return this date-time with fractional second part set back to 0,
   * i.e. time is truncated to seconds resolution
   * @see Floor */
  @Override // from RoundingInterface
  public DateTime floor() {
    return new DateTime(localDateTime.withNano(0));
  }

  @Override // from RoundingInterface
  public DateTime round() {
    int nano = localDateTime.getNano();
    return nano < 500_000_000 //
        ? new DateTime(localDateTime.withNano(0))
        : new DateTime(localDateTime.withNano(0).plusSeconds(1));
  }

  @Override // from AbstractScalar
  public Scalar subtract(Tensor tensor) {
    if (tensor instanceof DateTime dateTime)
      return seconds(Duration.between(dateTime.localDateTime, localDateTime));
    if (tensor instanceof Scalar scalar)
      return plus(scalar.negate());
    throw new Throw(this, tensor);
  }

  @Override // from AbstractScalar
  public Scalar multiply(Scalar scalar) {
    if (scalar.equals(one()))
      return this;
    throw new Throw(this, scalar);
  }

  @Override // from Scalar
  public Scalar negate() {
    throw new Throw(this);
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    throw new Throw(this);
  }

  @Override // from Scalar
  public Number number() {
    throw new Throw(this);
  }

  @Override // from Scalar
  public Scalar zero() {
    return ZERO;
  }

  @Override // from Scalar
  public Scalar one() {
    return RealScalar.ONE;
  }

  @Override // from Scalar
  protected Scalar plus(Scalar scalar) {
    return new DateTime(localDateTime.plus(duration(scalar)));
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DateTime dateTime)
      return localDateTime.compareTo(dateTime.localDateTime);
    throw new Throw(this, scalar);
  }

  @Override // from Object
  public int hashCode() {
    return localDateTime.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof DateTime dateTime //
        && localDateTime.equals(dateTime.localDateTime);
  }

  @Override // from Object
  public String toString() {
    return localDateTime.toString();
  }

  /** The parameter can be of the form:
   * DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS")
   * 
   * @param dateTimeFormatter
   * @return localDateTime().format(dateTimeFormatter) */
  public String format(DateTimeFormatter dateTimeFormatter) {
    return localDateTime.format(dateTimeFormatter);
  }
}
