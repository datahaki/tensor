// code by jph
package ch.ethz.idsc.tensor.num;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.AbsInterface;
import ch.ethz.idsc.tensor.api.ExactScalarQInterface;
import ch.ethz.idsc.tensor.api.ExpInterface;
import ch.ethz.idsc.tensor.api.LogInterface;
import ch.ethz.idsc.tensor.api.PowerInterface;
import ch.ethz.idsc.tensor.api.RoundingInterface;
import ch.ethz.idsc.tensor.api.SignInterface;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sign;

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

  /***************************************************/
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
    if (scalar instanceof Interval) {
      Interval interval = (Interval) scalar;
      return of( //
          clip.min().add(interval.clip.min()), //
          clip.max().add(interval.clip.max()));
    }
    return of( //
        clip.min().add(scalar), //
        clip.max().add(scalar));
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof Interval) {
      Interval interval = (Interval) scalar;
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

  /***************************************************/
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
      return BINARY_POWER.raise(this, optional.get());
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

  /***************************************************/
  @Override
  public int hashCode() {
    return clip.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Interval) {
      Interval interval = (Interval) object;
      return clip.equals(interval.clip);
    }
    return false;
  }

  @Override
  public String toString() {
    return clip.toString();
  }
}
