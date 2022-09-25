// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
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
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityCompatibleScalar;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Floor;

/** The purpose of {@link DateObject} is to allow the interoperation
 * between {@link LocalDateTime} and {@link Quantity} with temporal SI-unit,
 * for instance "s", "ms", "h", etc.
 * 
 * The difference between two {@link DateObject}s is a {@link Quantity} with
 * unit "s" encoded as {@link RationalScalar}, i.e. in exact precision.
 * 
 * In particular, that makes the use of type {@link Duration} obsolete.
 * 
 * An {@link DateObject} encodes an absolute point in a local calendar by
 * wrapping an instance of {@link LocalDateTime}.
 * 
 * Addition and subtraction of a quantity with temporal SI unit is supported.
 * This includes the units "s", "ms", "h", "days", ...
 * The result of the sum is a new instance of {@link DateObject}.
 * 
 * Addition of two instances of {@link DateObject} results in an exception.
 * Subtraction of two instances of {@link DateObject} results in a {@link Quantity}.
 * Negation of a {@link DateObject} results in an exception.
 * 
 * The string expression is of a date time scalar adheres to the pattern
 * uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS
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
 * based on the nano part of the {@link DateObject}. A nano part equal or
 * above 500_000_000 is rounded up, otherwise down.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DateObject.html">DateObject</a>
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class DateObject extends AbstractScalar implements //
    Comparable<Scalar>, QuantityCompatibleScalar, RoundingInterface, Serializable {
  private static final long NANOS_LONG = 1_000_000_000;
  private static final Scalar NANOS = RealScalar.of(NANOS_LONG);
  private static final Unit UNIT_S = Unit.of("s");
  private static final ScalarUnaryOperator TO_SECONDS = QuantityMagnitude.SI().in(UNIT_S);

  /** @param localDateTime
   * @return
   * @throws Exception if given localDateTime is null */
  public static DateObject of(LocalDateTime localDateTime) {
    return new DateObject(Objects.requireNonNull(localDateTime));
  }

  public static DateObject of(int year, Month month, int dayOfMonth, int hour, int minute) {
    return new DateObject(LocalDateTime.of(year, month, dayOfMonth, hour, minute));
  }

  public static DateObject of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
    return new DateObject(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second));
  }

  public static DateObject of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
    return new DateObject(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond));
  }

  public static DateObject of(int year, int month, int dayOfMonth, int hour, int minute) {
    return new DateObject(LocalDateTime.of(year, month, dayOfMonth, hour, minute));
  }

  public static DateObject of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
    return new DateObject(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second));
  }

  public static DateObject of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
    return new DateObject(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond));
  }

  /** @return instance based on LocalDateTime.now() */
  public static DateObject now() {
    return new DateObject(LocalDateTime.now());
  }

  /** @param scalar with time based SI-unit, for instance "s", "h", "days", "wk", "mo", "yr", etc.
   * @param zoneOffset
   * @return epoch of 1970-01-01T00:00:00Z plus the duration specified in given scalar relative to
   * given zoneOffset */
  public static DateObject ofEpoch(Scalar scalar, ZoneOffset zoneOffset) {
    Scalar seconds = TO_SECONDS.apply(scalar);
    Scalar floor = Floor.FUNCTION.apply(seconds);
    Scalar nanos = seconds.subtract(floor).multiply(NANOS);
    return new DateObject(LocalDateTime.ofEpochSecond( //
        floor.number().longValue(), //
        nanos.number().intValue(), //
        zoneOffset));
  }

  /** parsing function
   * 
   * Examples:
   * 2020-12-20T04:30:03.125239876 parses to {@link DateObject}
   * 
   * @param string
   * @return
   * @throws java.time.format.DateTimeParseException if {@code string} cannot be parsed
   * @see LocalDateTime */
  public static DateObject parse(String string) {
    return new DateObject(LocalDateTime.parse(string));
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
    scalar = TO_SECONDS.apply(scalar);
    Scalar integral = Floor.FUNCTION.apply(scalar);
    return Duration.ofSeconds( //
        Scalars.longValueExact(integral), //
        scalar.subtract(integral).multiply(NANOS).number().longValue());
  }

  // ---
  private final LocalDateTime localDateTime;

  private DateObject(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }

  /** @return instance of {@link LocalDateTime} backing this {@link DateObject} */
  public LocalDateTime localDateTime() {
    return localDateTime;
  }

  /** Careful: the below-second part is not considered for the output
   * 
   * function exists because the return type of
   * localDateTime.toEpochSecond(...) is long, i.e. without unit.
   * 
   * @param zoneOffset
   * @return the number of seconds from the epoch of 1970-01-01T00:00:00Z with unit "s"
   * which is negative for dates before that threshold */
  public Scalar toEpochWithoutNanos(ZoneOffset zoneOffset) {
    return Quantity.of(localDateTime.toEpochSecond(zoneOffset), UNIT_S);
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
  public DateObject plusYears(int years) {
    return new DateObject(localDateTime.plusYears(years));
  }

  /** Example:
   * 2004-01-31T00:00 plus 1 month == 2004-02-29T00:00
   * 
   * @param months to add, may be negative
   * @return this date-time with the months added, not null
   * @throws DateTimeException if the result exceeds the supported date range */
  public DateObject plusMonths(long months) {
    return new DateObject(localDateTime.plusMonths(months));
  }

  /** Example:
   * 2004-02-28T00:00 plus 1 day == 2004-02-29T00:00
   * 
   * @param days to add, may be negative
   * @return this date-time with the days added, not null
   * @throws DateTimeException if the result exceeds the supported date range */
  public DateObject plusDays(long days) {
    return new DateObject(localDateTime.plusDays(days));
  }

  @Override // from RoundingInterface
  public DateObject ceiling() {
    int nano = localDateTime.getNano();
    return 0 == nano //
        ? this
        : new DateObject(localDateTime.withNano(0).plusSeconds(1));
  }

  /** @return this date-time with fractional second part set back to 0,
   * i.e. time is truncated to seconds resolution */
  @Override // from RoundingInterface
  public DateObject floor() {
    return new DateObject(localDateTime.withNano(0));
  }

  @Override // from RoundingInterface
  public DateObject round() {
    int nano = localDateTime.getNano();
    return nano < 500_000_000 //
        ? new DateObject(localDateTime.withNano(0))
        : new DateObject(localDateTime.withNano(0).plusSeconds(1));
  }

  @Override // from AbstractScalar
  public Scalar subtract(Tensor tensor) {
    if (tensor instanceof DateObject dateObject)
      return seconds(Duration.between(dateObject.localDateTime, localDateTime));
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
    return Quantity.of(0, UNIT_S);
  }

  @Override // from Scalar
  public Scalar one() {
    return RealScalar.ONE;
  }

  /* method is deliberately public, and return type augmented to DateObject
   * to make obsolete a cast when using the pattern dateTime.add(delta) */
  @Override // from Scalar
  public DateObject plus(Scalar scalar) {
    return new DateObject(localDateTime.plus(duration(scalar)));
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DateObject dateObject)
      return localDateTime.compareTo(dateObject.localDateTime);
    throw new Throw(this, scalar);
  }

  @Override // from Object
  public int hashCode() {
    return localDateTime.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof DateObject dateObject //
        && localDateTime.equals(dateObject.localDateTime);
  }

  @Override // from Object
  public String toString() {
    return localDateTime.toString();
  }

  /** @param dateTimeFormatter
   * @return localDateTime().format(dateTimeFormatter) */
  public String format(DateTimeFormatter dateTimeFormatter) {
    return localDateTime.format(dateTimeFormatter);
  }
}
