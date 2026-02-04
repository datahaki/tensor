// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Optional;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Outer;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.InexactScalarMarker;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.MinMax;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.exp.LogInterface;
import ch.alpine.tensor.sca.pow.PowerInterface;

/** EXPERIMENTAL TENSOR
 * 
 * Careful: our implementation of CenteredInterval deviates from Mathematica
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CenteredInterval.html">CenteredInterval</a>
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class CenteredInterval extends AbstractScalar implements //
    AbsInterface, ExpInterface, LogInterface, InexactScalarMarker, //
    PowerInterface, SignInterface, Comparable<Scalar>, Serializable {
  private static final String SEPARATOR = "\u00B1";

  /** @param clip with [min, max]
   * @return */
  public static Scalar of(Clip clip) {
    return Scalars.isZero(clip.width()) //
        ? clip.min()
        : new CenteredInterval(clip);
  }

  /** @param center
   * @param radius
   * @return */
  public static Scalar of(Scalar center, Scalar radius) {
    return Scalars.isZero(radius) //
        ? center.add(radius.zero())
        : new CenteredInterval(center, radius);
  }

  /** @param center
   * @param radius
   * @return */
  public static Scalar of(Number center, Number radius) {
    return of(RealScalar.of(center), RealScalar.of(radius));
  }

  // ---
  private final Clip clip;
  private final Scalar center;
  private final Scalar radius;

  private CenteredInterval(Clip clip) {
    this.clip = clip;
    Scalar one = clip.min().one();
    radius = clip.width().divide(one.add(one));
    center = clip.min().add(radius);
  }

  private CenteredInterval(Scalar center, Scalar radius) {
    clip = Clips.centered(center, radius);
    this.center = center;
    this.radius = radius;
  }

  private CenteredInterval(Clip clip, Scalar mean, Scalar sigma) {
    this.clip = clip;
    this.center = mean;
    this.radius = sigma;
  }

  public Clip clip() {
    return clip;
  }

  public Scalar center() {
    return center;
  }

  public Scalar radius() {
    return radius;
  }

  @Override // from Scalar
  public Scalar negate() {
    return new CenteredInterval( //
        Clips.interval(clip.max().negate(), clip.min().negate()), //
        center.negate(), //
        radius);
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    if (clip.isInside(zero()))
      throw new Throw(this);
    return new CenteredInterval( //
        Clips.interval( //
            clip.max().reciprocal(), //
            clip.min().reciprocal()));
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof CenteredInterval centeredInterval //
        ? new CenteredInterval( //
            center.add(centeredInterval.center), //
            radius.add(centeredInterval.radius))
        : new CenteredInterval( //
            center.add(scalar), //
            radius);
  }

  private Tensor flat() {
    return Tensors.of(clip.min(), clip.max());
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof CenteredInterval centeredInterval //
        ? new CenteredInterval(Flatten.stream(Outer.of(Scalar::multiply, flat(), centeredInterval.flat()), 1).map(Scalar.class::cast).collect(MinMax.toClip()))
        : of(flat().multiply(scalar).stream().map(Scalar.class::cast).collect(MinMax.toClip()));
  }

  @Override // from Scalar
  public Scalar zero() {
    return center.zero();
  }

  @Override // from Scalar
  public Scalar one() {
    return center.one();
  }

  @Override // from Scalar
  public Number number() {
    throw new Throw(center, radius);
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() {
    Tensor flat = flat().map(Abs.FUNCTION);
    if (clip.isInside(zero()))
      flat.append(zero());
    return new CenteredInterval(flat.stream().map(Scalar.class::cast).collect(MinMax.toClip()));
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    Scalar abs = abs();
    return abs.multiply(abs);
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return new CenteredInterval(Clips.interval( //
        Exp.FUNCTION.apply(clip.min()), //
        Exp.FUNCTION.apply(clip.max())));
  }

  @Override // from LogInterface
  public Scalar log() {
    return new CenteredInterval(Clips.interval( //
        Log.FUNCTION.apply(clip.min()), //
        Log.FUNCTION.apply(clip.max())));
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
      return Scalars.mul().raise(this, optional.orElseThrow());
    throw new Throw(this, exponent);
  }

  @Override // from SignInterface
  public Scalar sign() {
    return of(Clips.interval( //
        Sign.FUNCTION.apply(clip.min()), //
        Sign.FUNCTION.apply(clip.max())));
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return clip.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof CenteredInterval centeredInterval //
        && clip.equals(centeredInterval.clip);
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
    return center + SEPARATOR + radius; // + "=" + clip.toString().substring(4);
  }

  @PackageTestAccess
  static Scalar centerAround(Clip clip, Scalar center) {
    return new CenteredInterval(center, Max.of( //
        Abs.between(clip.max(), center), //
        Abs.between(clip.min(), center)));
  }
}
