// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ChopInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.ArcTanInterface;

/** The string expression of {@link DurationScalar} is identical to that of {@link Duration}.
 * 
 * Example:
 * Duration.ofSeconds(245234, 123_236_987).toString() == "PT68H7M14.123236987S"
 * 
 * {@link Chop} treats duration scalar as decimal number of seconds.
 * For instance Chop_05[0sec+1nano]==0sec
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class DurationScalar extends AbstractScalar implements AbsInterface, //
    ArcTanInterface, ChopInterface, Comparable<Scalar>, SignInterface, Serializable {
  public static final DurationScalar ZERO = new DurationScalar(Duration.ZERO);

  /** @param duration
   * @return
   * @throws Exception if given duration is null */
  public static DurationScalar of(Duration duration) {
    return new DurationScalar(Objects.requireNonNull(duration));
  }

  /** Example:
   * fromSeconds(1) == PT1S
   * 
   * in case the scalar is in double precision, then the nano
   * seconds are approximated
   * 
   * @param scalar amount of seconds
   * @return
   * @throws Exception if scalar is not a {@link RealScalar} */
  public static DurationScalar fromSeconds(Scalar scalar) {
    Scalar integral = Floor.FUNCTION.apply(scalar);
    return new DurationScalar(Duration.ofSeconds( //
        Scalars.longValueExact(integral), //
        scalar.subtract(integral).multiply(NANOS).number().longValue()));
  }

  // ---
  /** Quote from the Duration javadoc:
   * "A duration of -1 nanosecond is stored as -1 seconds plus 999,999,999 nanoseconds." */
  private final Duration duration;

  /* package */ DurationScalar(Duration duration) {
    this.duration = duration;
  }

  /** @return duration */
  public Duration duration() {
    return duration;
  }

  @Override // from Scalar
  public DurationScalar multiply(Scalar scalar) {
    if (scalar instanceof DurationScalar) // condition inserted for clarity
      throw TensorRuntimeException.of(this, scalar);
    return fromSeconds(seconds().multiply(scalar));
  }

  @Override // from AbstractScalar
  public Scalar divide(Scalar scalar) {
    return scalar instanceof DurationScalar //
        ? seconds().divide(((DurationScalar) scalar).seconds())
        : fromSeconds(seconds().divide(scalar));
  }

  @Override // from AbstractScalar
  public Scalar under(Scalar scalar) {
    if (scalar instanceof DurationScalar)
      return ((DurationScalar) scalar).seconds().divide(seconds());
    if (scalar instanceof RealScalar) {
      RealScalar realScalar = (RealScalar) scalar;
      return realScalar.divide(seconds());
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override // from Scalar
  public DurationScalar negate() {
    return new DurationScalar(duration.negated());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return seconds().reciprocal();
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Scalar
  public DurationScalar zero() {
    return ZERO;
  }

  @Override // from Scalar
  public Scalar one() {
    return RealScalar.ONE;
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof DurationScalar)
      return new DurationScalar(duration.plus(((DurationScalar) scalar).duration));
    if (scalar instanceof DateTimeScalar)
      return scalar.add(this);
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override // from AbsInterface
  public Scalar abs() {
    return new DurationScalar(duration.abs());
  }

  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    if (x instanceof DurationScalar) {
      DurationScalar durationScalar = (DurationScalar) x;
      return ArcTan.of(durationScalar.seconds(), seconds());
    }
    if (x instanceof RealScalar) {
      RealScalar realScalar = (RealScalar) x;
      return ArcTan.of(realScalar, seconds());
    }
    throw TensorRuntimeException.of(x, this);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from ChopInterface
  public DurationScalar chop(Chop chop) {
    return chop.isZero(N.DOUBLE.apply(seconds())) //
        ? zero()
        : this;
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DurationScalar)
      return duration.compareTo(((DurationScalar) scalar).duration);
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override // from SignInterface
  public Scalar sign() {
    if (duration.isZero())
      return RealScalar.ZERO;
    return duration.isNegative() //
        ? RealScalar.ONE.negate()
        : RealScalar.ONE;
  }

  @Override // from Object
  public int hashCode() {
    return duration.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof DurationScalar //
        && duration.equals(((DurationScalar) object).duration);
  }

  @Override // from Object
  public String toString() {
    return duration.toString();
  }

  private static final long NANOS_LONG = 1_000_000_000;
  private static final Scalar NANOS = RealScalar.of(NANOS_LONG);

  /** Example:
   * DurationScalar.of(Duration.ofDays(-1)).seconds() == RealScalar.of(-24 * 60 * 60)
   * 
   * @return instance of rational scalar where fractional part corresponds to the nano seconds */
  public Scalar seconds() {
    return RealScalar.of(duration.getSeconds()) //
        .add(RationalScalar.of(duration.getNano(), NANOS_LONG));
  }
}
