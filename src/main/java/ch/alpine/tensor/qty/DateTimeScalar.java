// code by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;

/** EXPERIMENTAL
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class DateTimeScalar extends AbstractScalar implements Comparable<Scalar>, Serializable {
  public static Scalar fromString(String string) {
    try {
      return new DateTimeScalar(LocalDateTime.parse(string));
    } catch (Exception exception) {
      // ---
    }
    try {
      return DurationScalar.of(Duration.parse(string));
    } catch (Exception exception) {
      // ---
    }
    return Scalars.fromString(string);
  }

  private final LocalDateTime localDateTime;
  private final boolean negated;

  public DateTimeScalar(LocalDateTime localDateTime) {
    this(localDateTime, false);
  }

  private DateTimeScalar(LocalDateTime localDateTime, boolean negated) {
    this.localDateTime = localDateTime;
    this.negated = negated;
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    if (scalar.equals(RealScalar.ONE))
      return this;
    if (scalar.equals(RealScalar.ONE.negate()))
      return negate();
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar negate() {
    return new DateTimeScalar(localDateTime, !negated);
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
  public Scalar zero() {
    return DurationScalar.ZERO;
  }

  @Override
  public Scalar one() {
    return RealScalar.ONE;
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof DateTimeScalar) {
      DateTimeScalar dateTimeScalar = (DateTimeScalar) scalar;
      if (!negated && dateTimeScalar.negated)
        return DurationScalar.of(Duration.between(dateTimeScalar.localDateTime, localDateTime));
      if (negated && !dateTimeScalar.negated)
        return DurationScalar.of(Duration.between(localDateTime, dateTimeScalar.localDateTime));
      throw TensorRuntimeException.of(this, scalar);
    }
    if (scalar instanceof DurationScalar) {
      if (negated)
        throw TensorRuntimeException.of(this, scalar);
      DurationScalar durationScalar = (DurationScalar) scalar;
      return new DateTimeScalar(localDateTime.plus(durationScalar.duration()));
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public int compareTo(Scalar scalar) {
    if (scalar instanceof DateTimeScalar) {
      DateTimeScalar dateTimeScalar = (DateTimeScalar) scalar;
      return localDateTime.compareTo(dateTimeScalar.localDateTime);
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override
  public int hashCode() {
    return negated //
        ? -localDateTime.hashCode()
        : +localDateTime.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof DateTimeScalar) {
      DateTimeScalar dateTimeScalar = (DateTimeScalar) object;
      return localDateTime.equals(dateTimeScalar.localDateTime) //
          && negated == dateTimeScalar.negated;
    }
    return false;
  }

  @Override
  public String toString() {
    return negated //
        ? "-" + localDateTime.toString()
        : localDateTime.toString();
  }
}
