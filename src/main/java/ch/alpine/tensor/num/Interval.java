// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ExactScalarQInterface;
import ch.alpine.tensor.api.ExpInterface;
import ch.alpine.tensor.api.LogInterface;
import ch.alpine.tensor.api.PowerInterface;
import ch.alpine.tensor.api.RoundingInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;

/** EXPERIMENTAL multiple clip */
/* package */ class Interval extends AbstractScalar implements //
    AbsInterface, ExactScalarQInterface, ExpInterface, LogInterface, PowerInterface, RoundingInterface, SignInterface {
  private static final BinaryPower<Scalar> BINARY_POWER = new BinaryPower<>(ScalarProduct.INSTANCE);

  /** @param clip
   * @return */
  public static Scalar of(Clip clip) {
    return new Interval(Objects.requireNonNull(clip));
  }

  public static Scalar of(Number min, Number max) {
    return of(RealScalar.of(min), RealScalar.of(max));
  }

  private static Scalar of(Scalar min, Scalar max) {
    return new Interval(Clips.interval(min, max));
  }

  // ---
  private final Clip clip;

  private Interval(Clip clip) {
    this.clip = clip;
  }

  @Override
  public Scalar negate() {
    return new Interval(Clips.interval( //
        clip.max().negate(), //
        clip.min().negate()));
  }

  @Override
  public Scalar reciprocal() {
    if (clip.isInside(zero()))
      throw TensorRuntimeException.of(this);
    return of( //
        clip.max().reciprocal(), //
        clip.min().reciprocal());
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof Interval interval //
        ? of( //
            clip.min().add(interval.clip.min()), //
            clip.max().add(interval.clip.max()))
        : of( //
            clip.min().add(scalar), //
            clip.max().add(scalar));
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof Interval interval) {
      Tensor va = Tensors.of(clip.min(), clip.max());
      Tensor vb = Tensors.of(interval.clip.min(), interval.clip.max());
      ScalarSummaryStatistics scalarSummaryStatistics = TensorProduct.of(va, vb).flatten(1) //
          .map(Scalar.class::cast) //
          .collect(ScalarSummaryStatistics.collector());
      return of( //
          scalarSummaryStatistics.getMin(), //
          scalarSummaryStatistics.getMax());
    }
    Scalar pa = clip.min().multiply(scalar);
    Scalar pb = clip.max().multiply(scalar);
    return of( //
        Min.of(pa, pb), //
        Max.of(pa, pb));
  }

  @Override
  public Scalar zero() {
    return clip.min().zero();
  }

  @Override
  public Scalar one() {
    return clip.min().one();
  }

  @Override
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() {
    Scalar pa = Abs.FUNCTION.apply(clip.min());
    Scalar pb = Abs.FUNCTION.apply(clip.max());
    Scalar max = Max.of(pa, pb);
    if (clip.isInside(zero()))
      return new Interval(Clips.positive(max));
    return of(Min.of(pa, pb), max);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    Scalar pa = AbsSquared.FUNCTION.apply(clip.min());
    Scalar pb = AbsSquared.FUNCTION.apply(clip.max());
    Scalar max = Max.of(pa, pb);
    if (clip.isInside(zero()))
      return new Interval(Clips.positive(max));
    return of(Min.of(pa, pb), max);
  }

  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return ExactScalarQ.of(clip.min()) //
        && ExactScalarQ.of(clip.max());
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return of( //
        Exp.FUNCTION.apply(clip.min()), //
        Exp.FUNCTION.apply(clip.max()));
  }

  @Override // from LogInterface
  public Scalar log() {
    return of( //
        Log.FUNCTION.apply(clip.min()), //
        Log.FUNCTION.apply(clip.max()));
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    Optional<BigInteger> optional = Scalars.optionalBigInteger(exponent);
    if (optional.isPresent())
      return BINARY_POWER.raise(this, optional.orElseThrow());
    throw TensorRuntimeException.of(this);
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return of( //
        Ceiling.FUNCTION.apply(clip.min()), //
        Ceiling.FUNCTION.apply(clip.max()));
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return of( //
        Floor.FUNCTION.apply(clip.min()), //
        Floor.FUNCTION.apply(clip.max()));
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return of( //
        Round.FUNCTION.apply(clip.min()), //
        Round.FUNCTION.apply(clip.max()));
  }

  @Override // from SignInterface
  public Scalar sign() {
    return of( //
        Sign.FUNCTION.apply(clip.min()), //
        Sign.FUNCTION.apply(clip.max()));
  }

  // ---
  @Override
  public int hashCode() {
    return clip.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Interval interval //
        && clip.equals(interval.clip);
  }

  @Override
  public String toString() {
    return clip.toString();
  }
}
