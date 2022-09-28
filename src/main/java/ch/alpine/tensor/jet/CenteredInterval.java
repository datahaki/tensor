// code by jph
package ch.alpine.tensor.jet;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.InexactScalarMarker;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.num.ScalarProduct;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.red.MinMax;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.exp.LogInterface;
import ch.alpine.tensor.sca.pow.PowerInterface;

/** EXPERIMENTAL multiple clip
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
// @Deprecated // since mathematica defines Interval as union of closed intervals
// ... functionality here should be moved to CenteredInterval
/* package */ class CenteredInterval extends AbstractScalar implements //
    AbsInterface, ExpInterface, LogInterface, InexactScalarMarker, //
    PowerInterface, SignInterface, Comparable<Scalar> {
  private static final BinaryPower<Scalar> BINARY_POWER = new BinaryPower<>(ScalarProduct.INSTANCE);
  private static final String SEPARATOR = "\u00B1";

  /** @param clip
   * @return */
  public static Scalar of(Clip clip) {
    return new CenteredInterval(Objects.requireNonNull(clip));
  }

  public static Scalar of(Scalar mean, Scalar sigma) {
    return new CenteredInterval(mean, sigma);
  }

  public static Scalar of(Number mean, Number sigma) {
    return of(RealScalar.of(mean), RealScalar.of(sigma));
  }

  // ---
  private final Clip clip;
  private final Scalar mean;
  private final Scalar sigma;

  private CenteredInterval(Clip clip) {
    this.clip = clip;
    sigma = clip.width().multiply(RationalScalar.HALF);
    mean = clip.min().add(sigma);
  }

  private CenteredInterval(Scalar mean, Scalar sigma) {
    this.mean = mean;
    this.sigma = sigma;
    clip = Clips.interval(mean.subtract(sigma), mean.add(sigma));
  }

  @Override
  public Scalar negate() {
    return new CenteredInterval(Clips.interval( //
        clip.max().negate(), //
        clip.min().negate()));
  }

  @Override
  public Scalar reciprocal() {
    if (clip.isInside(zero()))
      throw new Throw(this);
    return new CenteredInterval( //
        Clips.interval( //
            clip.max().reciprocal(), //
            clip.min().reciprocal()));
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof CenteredInterval interval //
        ? of( //
            clip.min().add(interval.clip.min()), //
            clip.max().add(interval.clip.max()))
        : of( //
            clip.min().add(scalar), //
            clip.max().add(scalar));
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof CenteredInterval interval) {
      Tensor va = Tensors.of(clip.min(), clip.max());
      Tensor vb = Tensors.of(interval.clip.min(), interval.clip.max());
      return new CenteredInterval(TensorProduct.of(va, vb).flatten(1) //
          .map(Scalar.class::cast) //
          .collect(MinMax.toClip()));
    }
    Scalar pa = clip.min().multiply(scalar);
    Scalar pb = clip.max().multiply(scalar);
    return of( //
        Min.of(pa, pb), //
        Max.of(pa, pb));
  }

  @Override // from Scalar
  public Scalar zero() {
    return mean.zero();
  }

  @Override // from Scalar
  public Scalar one() {
    return mean.one();
  }

  @Override // from Scalar
  public Number number() {
    throw new Throw(mean, sigma);
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() {
    Scalar pa = Abs.FUNCTION.apply(clip.min());
    Scalar pb = Abs.FUNCTION.apply(clip.max());
    Scalar max = Max.of(pa, pb);
    if (clip.isInside(zero()))
      return new CenteredInterval(Clips.positive(max));
    return of(Min.of(pa, pb), max);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    Scalar pa = AbsSquared.FUNCTION.apply(clip.min());
    Scalar pb = AbsSquared.FUNCTION.apply(clip.max());
    Scalar max = Max.of(pa, pb);
    if (clip.isInside(zero()))
      return new CenteredInterval(Clips.positive(max));
    return of(Min.of(pa, pb), max);
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

  @Override // from InexactScalarMarker
  public boolean isFinite() {
    return FiniteScalarQ.of(clip.min()) //
        && FiniteScalarQ.of(clip.max());
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    Optional<BigInteger> optional = Scalars.optionalBigInteger(exponent);
    if (optional.isPresent())
      return BINARY_POWER.raise(this, optional.orElseThrow());
    throw new Throw(this, exponent);
  }

  @Override // from SignInterface
  public Scalar sign() {
    return of( //
        Sign.FUNCTION.apply(clip.min()), //
        Sign.FUNCTION.apply(clip.max()));
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return clip.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof CenteredInterval interval //
        && clip.equals(interval.clip);
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    if (scalar instanceof CenteredInterval centeredInterval) {
      if (Scalars.lessThan(centeredInterval.clip.max(), clip.min()))
        return +1;
      if (Scalars.lessThan(clip.max(), centeredInterval.clip.min()))
        return -1;
      throw new Throw(clip, scalar);
    }
    if (Scalars.lessThan(scalar, clip.min()))
      return +1;
    if (Scalars.lessThan(clip.max(), scalar))
      return -1;
    throw new Throw(clip, scalar);
  }

  @Override // from Object
  public String toString() {
    return mean + SEPARATOR + sigma + "=" + clip.toString().substring(4);
  }
}
