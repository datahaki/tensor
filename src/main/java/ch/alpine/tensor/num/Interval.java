// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
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
/* package */ class Interval extends MultiplexScalar implements //
    AbsInterface, ExpInterface, LogInterface, PowerInterface, SignInterface {
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
      throw new Throw(this);
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
    throw new Throw(this, exponent);
  }

  @Override // from SignInterface
  public Scalar sign() {
    return of( //
        Sign.FUNCTION.apply(clip.min()), //
        Sign.FUNCTION.apply(clip.max()));
  }

  @Override // from MultiplexScalar
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return of( //
        unaryOperator.apply(clip.min()), //
        unaryOperator.apply(clip.max()));
  }

  @Override // from MultiplexScalar
  public boolean allMatch(Predicate<Scalar> predicate) {
    return predicate.test(clip.min()) //
        && predicate.test(clip.max());
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return clip.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof Interval interval //
        && clip.equals(interval.clip);
  }

  @Override // from Object
  public String toString() {
    return clip.toString();
  }
}
