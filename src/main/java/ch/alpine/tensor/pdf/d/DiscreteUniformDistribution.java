// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.BigIntegerMath;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;

/** Careful:
 * In Mathematica::DiscreteUniformDistribution the upper bound "max" is inclusive.
 * The tensor library considers "max" to be excluded.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DiscreteUniformDistribution.html">DiscreteUniformDistribution</a> */
public class DiscreteUniformDistribution extends AbstractDiscreteDistribution implements Serializable {
  private static final Scalar _12 = RealScalar.of(12);

  /** Example:
   * PDF[DiscreteUniformDistribution[{0, 10}], x] == 1/10 for 0 <= x < 10 and x integer
   * 
   * @param min inclusive
   * @param max exclusive and min < max
   * @return distribution */
  public static Distribution of(Scalar min, Scalar max) {
    return of(Scalars.bigIntegerValueExact(min), Scalars.bigIntegerValueExact(max));
  }

  /** @param min inclusive
   * @param max exclusive and min < max
   * @return distribution */
  public static Distribution of(BigInteger min, BigInteger max) {
    if (min.compareTo(max) < 0)
      return new DiscreteUniformDistribution(min, max);
    throw new Throw(min, max);
  }

  /** @param min inclusive
   * @param max exclusive and min < max
   * @return distribution */
  public static Distribution of(int min, int max) {
    return of( //
        BigInteger.valueOf(min), //
        BigInteger.valueOf(max));
  }

  // ---
  private final Clip clip;
  private final Scalar max_1; // exclusive
  private final BigInteger width_1; // exclusive
  private final Scalar p;

  private DiscreteUniformDistribution(BigInteger min, BigInteger max) {
    clip = Clips.interval(min, max);
    max_1 = clip.max().subtract(RealScalar.ONE);
    width_1 = max.subtract(min).subtract(BigInteger.ONE);
    p = clip.width().reciprocal();
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return max_1.add(clip.min()).multiply(RationalScalar.HALF);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    Scalar width = RealScalar.of(width_1);
    return RealScalar.TWO.add(width).multiply(width).divide(_12);
  }

  @Override // from DiscreteDistribution
  public BigInteger lowerBound() {
    return Scalars.bigIntegerValueExact(clip.min());
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return clip.min().add(RealScalar.of(random(width_1, randomGenerator)));
  }

  /** @param max_inclusive
   * @param randomGenerator
   * @return random BigInteger from 0, 1, ..., max_inclusive */
  @PackageTestAccess
  /* package */ static BigInteger random(BigInteger max_inclusive, RandomGenerator randomGenerator) {
    BigInteger bigInteger;
    do {
      bigInteger = BigIntegerMath.random(max_inclusive.bitLength(), randomGenerator);
    } while (0 < bigInteger.compareTo(max_inclusive));
    return bigInteger;
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return p.equals(RealScalar.ONE) //
        ? clip.max().subtract(RealScalar.ONE) // consistent with Mathematica
        : protected_quantile(Clips.unit().requireInside(p));
  }

  @Override // from InverseCDF
  protected Scalar protected_quantile(Scalar q) {
    return clip.min().add(Max.of(RealScalar.ONE, Ceiling.FUNCTION.apply(clip.width().multiply(q)))).subtract(RealScalar.ONE);
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger x) {
    return Scalars.lessThan(RealScalar.of(x), clip.max()) //
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
