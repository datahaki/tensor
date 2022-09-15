// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityCompatibleScalar;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Floor;

/** encodes an absolute point in a local calendar by wrapping an instance of
 * {@link LocalDateTime}
 * 
 * Addition and subtraction of a quantity with temporal SI unit is supported.
 * This includes the units "s", "ms", "h", "days", ...
 * The result of the sum is a new instance of {@link DateTimeScalar}.
 * 
 * Addition of two instances of {@link DateTimeScalar} results in an exception.
 * Subtraction of two instances of {@link DateTimeScalar} results in a {@link Quantity}.
 * Negation of a {@link DateTimeScalar} results in an exception.
 * 
 * <p>similar to
 * <a href="https://reference.wolfram.com/language/ref/DateObject.html">DateObject</a>
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class DateTimeScalar extends AbstractScalar implements //
    Comparable<Scalar>, QuantityCompatibleScalar, Serializable {
  /* package */ static final Unit UNIT_S = Unit.of("s");
  /* package */ static final long NANOS_LONG = 1_000_000_000;
  /* package */ static final Scalar NANOS = RealScalar.of(NANOS_LONG);
  /* package */ static final ScalarUnaryOperator TO_SECONDS = QuantityMagnitude.SI().in(UNIT_S);

  /** @param localDateTime
   * @return
   * @throws Exception if given localDateTime is null */
  public static DateTimeScalar of(LocalDateTime localDateTime) {
    return new DateTimeScalar(Objects.requireNonNull(localDateTime));
  }

  /** @param scalar
   * @param zoneOffset
   * @return */
  public static DateTimeScalar ofEpochSecond(Scalar scalar, ZoneOffset zoneOffset) {
    Scalar seconds = TO_SECONDS.apply(scalar);
    Scalar floor = Floor.FUNCTION.apply(seconds);
    Scalar nanos = seconds.subtract(floor).multiply(DateTimeScalar.NANOS);
    return new DateTimeScalar(LocalDateTime.ofEpochSecond( //
        floor.number().longValue(), //
        nanos.number().intValue(), //
        zoneOffset));
  }

  // ---
  private final LocalDateTime localDateTime;

  /* package */ DateTimeScalar(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }

  /** @return instance of {@link LocalDateTime} backing this {@link DateTimeScalar} */
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
  public Scalar toEpochSecondFloor(ZoneOffset zoneOffset) {
    return Quantity.of(localDateTime.toEpochSecond(zoneOffset), UNIT_S);
  }

  /** @param zoneOffset
   * @return seconds from the epoch of 1970-01-01T00:00:00Z with unit "s"
   * which is negative for dates before that threshold in exact precision */
  public Scalar toEpochSecond(ZoneOffset zoneOffset) {
    Scalar nanos = RationalScalar.of(localDateTime.getNano(), DateTimeScalar.NANOS_LONG);
    return Quantity.of( //
        RealScalar.of(localDateTime.toEpochSecond(zoneOffset)).add(nanos), UNIT_S);
  }

  @Override // from AbstractScalar
  public Scalar subtract(Tensor tensor) {
    if (tensor instanceof DateTimeScalar) {
      DateTimeScalar dateTimeScalar = (DateTimeScalar) tensor;
      return TemporalScalars.seconds(Duration.between(dateTimeScalar.localDateTime, localDateTime));
    }
    if (tensor instanceof Scalar) {
      Scalar scalar = (Scalar) tensor;
      return new DateTimeScalar(localDateTime.minus(TemporalScalars.duration(scalar)));
    }
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

  @Override // from Scalar
  protected Scalar plus(Scalar scalar) {
    return new DateTimeScalar(localDateTime.plus(TemporalScalars.duration(scalar)));
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DateTimeScalar)
      return localDateTime.compareTo(((DateTimeScalar) scalar).localDateTime);
    throw new Throw(this, scalar);
  }

  @Override // from Object
  public int hashCode() {
    return localDateTime.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof DateTimeScalar //
        && localDateTime.equals(((DateTimeScalar) object).localDateTime);
  }

  @Override // from Object
  public String toString() {
    return localDateTime.toString();
  }
}
