// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.RandomInteger;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;

/** Consistent with Mathematica:
 * In Mathematica::DiscreteUniformDistribution the upper bound "max" is inclusive.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DiscreteUniformDistribution.html">DiscreteUniformDistribution</a> */
public class DiscreteUniformDistribution extends AbstractDiscreteDistribution implements Serializable {
  private static final Scalar _12 = RealScalar.of(12);

  /** Example:
   * PDF[DiscreteUniformDistribution[{0, 10}], x] == 1/10 for 0 <= x <= 10 and x integer
   * 
   * @param min inclusive
   * @param max inclusive and min <= max
   * @return distribution */
  public static Distribution of(Scalar min, Scalar max) {
    return of( //
        Scalars.bigIntegerValueExact(min), //
        Scalars.bigIntegerValueExact(max));
  }

  public static Distribution of(Clip clip) {
    return of(clip.min(), clip.max());
  }

  /** @param min inclusive
   * @param max inclusive and min <= max
   * @return distribution */
  public static Distribution of(BigInteger min, BigInteger max) {
    if (min.compareTo(max) <= 0)
      return new DiscreteUniformDistribution(min, max);
    throw new Throw(min, max);
  }

  /** @param min inclusive
   * @param max inclusive and min <= max
   * @return distribution */
  public static Distribution of(int min, int max) {
    return of( //
        BigInteger.valueOf(min), //
        BigInteger.valueOf(max));
  }

  public static Distribution forArray(int max_exclusive) {
    return of(0, Integers.requirePositive(max_exclusive) - 1);
  }

  public static Distribution forArray(BigInteger max_exclusive) {
    return of(BigInteger.ZERO, max_exclusive.subtract(BigInteger.ONE));
  }

  // ---
  private final Clip clip;
  private final BigInteger width;
  private final Scalar p;

  private DiscreteUniformDistribution(BigInteger min, BigInteger max) {
    clip = Clips.interval(min, max);
    width = Scalars.bigIntegerValueExact(clip.width());
    p = clip.width().add(RealScalar.ONE).reciprocal();
  }

  @Override // from UnivariateDistribution
  public Clip support() {
    return clip;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return clip.width().multiply(Rational.HALF).add(clip.min());
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return RealScalar.TWO.add(clip.width()).multiply(clip.width()).divide(_12);
  }

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return clip.min().add(RealScalar.of(RandomInteger.of(width, randomGenerator)));
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return p.equals(RealScalar.ONE) //
        ? clip.max() // consistent with Mathematica
        : protected_quantile(Clips.unit().requireInside(p));
  }

  @Override // from InverseCDF
  protected Scalar protected_quantile(Scalar q) {
    return clip.min().add(Max.of(RealScalar.ONE, Ceiling.FUNCTION.apply(clip.width().add(RealScalar.ONE).multiply(q)))).subtract(RealScalar.ONE);
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger x) {
    return Scalars.lessEquals(RealScalar.of(x), clip.max()) //
        ? p
        : RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar num = Ceiling.FUNCTION.apply(x).subtract(clip.min());
    return Clips.unit().apply(num.multiply(p));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    Scalar num = RealScalar.ONE.add(Floor.FUNCTION.apply(x)).subtract(clip.min());
    return Clips.unit().apply(num.multiply(p));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("DiscreteUniformDistribution", clip.min(), clip.max());
  }
}
