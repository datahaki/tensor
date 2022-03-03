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
import ch.alpine.tensor.red.CentralMoment;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

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
        ? DiracDistribution.of(clip.min())
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
    Distribution distribution = TrapezoidalDistribution.of(clip.min(), clip.min(), clip.max(), clip.max());
    return CentralMoment.of(distribution, order);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar value = clip.isInside(x) //
        ? clip.width().reciprocal()
        : RealScalar.ZERO;
    return x.one().multiply(value);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return clip.rescale(x);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    return p.multiply(clip.width()).add(clip.min());
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s]", getClass().getSimpleName(), clip.min(), clip.max());
  }
}
