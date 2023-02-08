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
import ch.alpine.tensor.sca.Ceiling;
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
  private final BigInteger min; // inclusive
  private final Scalar _min;
  private final BigInteger max; // exclusive
  private final BigInteger max_1; // exclusive
  private final Scalar p; // precomputed

  private DiscreteUniformDistribution(BigInteger min, BigInteger max) {
    this.min = min;
    this._min = RealScalar.of(min);
    this.max = max;
    max_1 = max.subtract(BigInteger.ONE);
    p = RationalScalar.of(BigInteger.ONE, max.subtract(min));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RationalScalar.of(max_1.add(min), BigInteger.TWO);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    Scalar width = RealScalar.of(max_1.subtract(min));
    return RealScalar.TWO.add(width).multiply(width).divide(_12);
  }

  @Override // from DiscreteDistribution
  public BigInteger lowerBound() {
    return min;
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(RandomGenerator random) {
    return RealScalar.of(min.add(random(max.subtract(min), random)));
  }

  /** @param limit
   * @param random
   * @return random BigInteger from 0, 1, ..., limit - 1 */
  @PackageTestAccess
  /* package */ static BigInteger random(BigInteger limit, RandomGenerator random) {
    BigInteger max = limit.subtract(BigInteger.ONE);
    BigInteger bigInteger;
    do {
      bigInteger = BigIntegerMath.random(max.bitLength(), random);
    } while (0 < bigInteger.compareTo(max));
    return bigInteger;
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return p.equals(RealScalar.ONE) //
        ? RealScalar.of(max_1) // consistent with Mathematica
        : protected_quantile(Clips.unit().requireInside(p));
  }

  @Override // from InverseCDF
  protected Scalar protected_quantile(Scalar q) {
    return _min.add(Floor.FUNCTION.apply(q.multiply(p.reciprocal()))); // do not simplify
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger x) {
    return x.compareTo(max) < 0 // x < max //
        ? p
        : RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar num = Ceiling.FUNCTION.apply(x).subtract(_min);
    return Clips.unit().apply(num.multiply(p));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    Scalar num = RealScalar.ONE.add(Floor.FUNCTION.apply(x)).subtract(_min);
    return Clips.unit().apply(num.multiply(p));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("DiscreteUniformDistribution", min, max);
  }
}
