// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.KurtosisInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.StandardDeviationInterface;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** uniform distribution over continuous interval [a, b].
 * 
 * <p>InverseCDF is defined over interval [0, 1]
 * 
 * UniformDistribution is a special {@link TrapezoidalDistribution0}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UniformDistribution.html">UniformDistribution</a> */
public class UniformDistribution extends AbstractContinuousDistribution //
    implements StandardDeviationInterface, CentralMomentInterface, KurtosisInterface, Serializable {
  private static final Scalar _1_12 = Rational.of(1, 12);

  /** the input parameters may be an instance of {@link Quantity} of identical unit
   * 
   * @param min < max
   * @param max
   * @return uniform distribution over the half-open interval [min, max)
   * @throws Exception if max - min is not finite
   * @see FiniteScalarQ */
  public static Distribution of(Scalar min, Scalar max) {
    return of(Clips.interval(min, max));
  }

  /** @param min < max
   * @param max
   * @return uniform distribution over the half-open interval [min, max)
   * @throws Exception if max - min is not finite
   * @see FiniteScalarQ */
  public static Distribution of(Number min, Number max) {
    return of(Clips.interval(min, max));
  }

  /** @param clip
   * @return uniform distribution over the half-open interval [clip.min(), clip.max())
   * @throws Exception if {@link Clip#width()} is not finite
   * @see FiniteScalarQ */
  public static Distribution of(Clip clip) {
    Scalar width = clip.width();
    if (Scalars.isZero(width))
      return DiracDeltaDistribution.of(clip.min());
    FiniteScalarQ.require(width);
    return new UniformDistribution(clip);
  }

  // ---
  private static class UnitDouble extends UniformDistribution {
    UnitDouble() {
      super(Clips.unit());
    }

    @Override // from UniformDistribution
    protected Scalar protected_quantile(Scalar p) {
      return p;
    }
  }

  private static final Distribution UNIT = new UnitDouble();

  /** @return uniform distribution over the half-open unit interval [0, 1) */
  public static Distribution unit() {
    return UNIT;
  }

  // ---
  private static class UnitDecimal extends UnitDouble {
    private final int digits;
    private final Distribution distribution;

    public UnitDecimal(int digits) {
      this.digits = Integers.requirePositive(digits);
      distribution = DiscreteUniformDistribution.forArray(Scalars.bigIntegerValueExact(Power.of(10, digits)));
    }

    @Override // from AbstractContinuousDistribution
    public Scalar randomVariate(RandomGenerator randomGenerator) {
      String num = RandomVariate.of(distribution, randomGenerator).toString();
      return DecimalScalar.of(new BigDecimal("0." + "0".repeat(digits - num.length()) + num));
    }
  }

  /** @return uniform distribution over the half-open unit interval [0, 1) */
  public static Distribution unit(int digits) {
    return new UnitDecimal(digits);
  }

  // ---
  private final Clip clip;

  /** @param clip guaranteed to be non-null and to have non-zero width */
  private UniformDistribution(Clip clip) {
    this.clip = clip;
  }

  /** @return support of distribution */
  @Override
  public final Clip support() {
    return clip;
  }

  @Override // from PDF
  public final Scalar at(Scalar x) {
    Scalar inverse = clip.width().reciprocal();
    return x.one().multiply(clip.isInside(x) ? inverse : inverse.zero());
  }

  @Override // from CDF
  public final Scalar p_lessThan(Scalar x) {
    return clip.rescale(x);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    return p.multiply(clip.width()).add(clip.min());
  }

  @Override // from MeanInterface
  public final Scalar mean() {
    return protected_quantile(Rational.HALF);
  }

  @Override // from VarianceInterface
  public final Scalar variance() {
    return clip.width().multiply(clip.width()).multiply(_1_12);
  }

  @Override // from StandardDeviationInterface
  public final Scalar standardDeviation() {
    return clip.width().multiply(Sqrt.FUNCTION.apply(_1_12));
  }

  @Override // from KurtosisInterface
  public Scalar kurtosis() {
    return Rational.of(9, 5);
  }

  @Override // from CentralMomentInterface
  public Scalar centralMoment(int order) {
    // for k even the central moment == (2^-k ( b-a )^k)/(1 + k)
    // for k uneven 0
    // consistent with Polynomial # moment
    return order % 2 == 0 //
        ? Power.of(clip.width().multiply(Rational.HALF), order).divide(RealScalar.of(order + 1))
        : Power.of(clip.width().zero(), order);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("UniformDistribution", clip.min(), clip.max());
  }
}
