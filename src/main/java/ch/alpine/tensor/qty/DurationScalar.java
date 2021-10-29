// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.time.Duration;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.AbsInterface;

/** EXPERIMENTAL
 * 
 * The string expression of {@link DurationScalar} is identical to that of {@link Duration}.
 * 
 * Example:
 * Duration.ofSeconds(245234, 123_236_987).toString() == "PT68H7M14.123236987S"
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class DurationScalar extends AbstractScalar implements AbsInterface, //
    Comparable<Scalar>, Serializable {
  public static final DurationScalar ZERO = new DurationScalar(Duration.ZERO);

  public static DurationScalar of(Duration duration) {
    return new DurationScalar(duration);
  }

  // ---
  /** Quote from the Duration javadoc:
   * "A duration of -1 nanosecond is stored as -1 seconds plus 999,999,999 nanoseconds." */
  private final Duration duration;

  private DurationScalar(Duration duration) {
    this.duration = duration;
  }

  public Duration duration() {
    return duration;
  }

  @Override
  public DurationScalar multiply(Scalar scalar) {
    return new DurationScalar(duration.multipliedBy(IntegerQ.require(scalar).number().longValue()));
  }

  @Override
  public Scalar divide(Scalar scalar) {
    if (scalar instanceof DurationScalar) {
      DurationScalar durationScalar = (DurationScalar) scalar;
      return toSeconds().divide(durationScalar.toSeconds());
    }
    // TODO can to better
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override
  public Scalar under(Scalar scalar) {
    if (scalar instanceof DurationScalar) {
      DurationScalar durationScalar = (DurationScalar) scalar;
      return durationScalar.toSeconds().divide(toSeconds());
    }
    // TODO can to better
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override
  public DurationScalar negate() {
    return new DurationScalar(duration.negated());
  }

  @Override
  public Scalar reciprocal() {
    throw TensorRuntimeException.of(this);
  }

  @Override
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  @Override
  public DurationScalar zero() {
    return ZERO;
  }

  @Override
  public Scalar one() {
    return RealScalar.ONE;
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof DurationScalar) {
      DurationScalar durationScalar = (DurationScalar) scalar;
      return new DurationScalar(duration.plus(durationScalar.duration));
    }
    if (scalar instanceof DateTimeScalar)
      return scalar.add(this);
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override // from AbsInterface
  public Scalar abs() {
    return new DurationScalar(duration.abs());
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    throw TensorRuntimeException.of(this);
  }

  @Override
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DurationScalar) {
      DurationScalar durationScalar = (DurationScalar) scalar;
      return duration.compareTo(durationScalar.duration);
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override
  public int hashCode() {
    return duration.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof DurationScalar) {
      DurationScalar durationScalar = (DurationScalar) object;
      return duration.equals(durationScalar.duration);
    }
    return false;
  }

  @Override
  public String toString() {
    return duration.toString();
  }

  private Scalar toSeconds() {
    return RealScalar.of(duration.getSeconds()).add(RationalScalar.of(duration.getNano(), 1_000_000_000));
  }
}
