// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.pdf.CentralMomentInterface;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.KurtosisInterface;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;

/** uniform distribution over continuous interval [a, b].
 * 
 * <p>InverseCDF is defined over interval [0, 1]
 * 
 * UniformDistribution is a special {@link TrapezoidalDistribution}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UniformDistribution.html">UniformDistribution</a> */
public class UniformDistribution extends AbstractContinuousDistribution //
    implements CentralMomentInterface, KurtosisInterface, Serializable {
  private static final Scalar _1_12 = RationalScalar.of(1, 12);
  private static final Distribution UNIT = new UniformDistribution(Clips.unit());

  /** the input parameters may be instance of {@link Quantity} of identical unit
   * 
   * @param min < max
   * @param max
   * @return uniform distribution over the half-open interval [min, max) */
  public static Distribution of(Scalar min, Scalar max) {
    return of(Clips.interval(min, max));
  }

  /** @param min < max
   * @param max
   * @return uniform distribution over the half-open interval [min, max) */
  public static Distribution of(Number min, Number max) {
    return of(Clips.interval(min, max));
  }

  /** @param clip
   * @return uniform distribution over the half-open interval [clip.min(), clip.max()) */
  public static Distribution of(Clip clip) {
    return Scalars.isZero(clip.width()) //
        ? DiracDeltaDistribution.of(clip.min())
        : new UniformDistribution(clip);
  }

  /** @return uniform distribution over the half-open unit interval [0, 1) */
  public static Distribution unit() {
    return UNIT;
  }

  // ---
  private final Clip clip;

  /** @param clip guaranteed to be non-null and to have non-zero width */
  private UniformDistribution(Clip clip) {
    this.clip = clip;
  }

  /** @return support of distribution */
  public Clip support() {
    return clip;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar inverse = clip.width().reciprocal();
    return x.one().multiply(clip.isInside(x) ? inverse : inverse.zero());
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return clip.rescale(x);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    return p.multiply(clip.width()).add(clip.min());
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return protected_quantile(RationalScalar.HALF);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return clip.width().multiply(clip.width()).multiply(_1_12);
  }

  @Override // from KurtosisInterface
  public Scalar kurtosis() {
    return RationalScalar.of(9, 5);
  }

  @Override // from CentralMomentInterface
  public Scalar centralMoment(int order) {
    // for k even the central moment == (2^-k ( b-a )^k)/(1 + k)
    // for k uneven 0
    return order % 2 == 0 //
        ? Power.of(clip.width().multiply(RationalScalar.HALF), order).divide(RealScalar.of(order + 1))
        : Power.of(clip.width().zero(), order);
  }

  @Override // from Object
  public String toString() {
    return String.format("UniformDistribution[%s, %s]", clip.min(), clip.max());
  }
}
