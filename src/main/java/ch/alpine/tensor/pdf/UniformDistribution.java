// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** uniform distribution over continuous interval [a, b].
 * 
 * <p>InverseCDF is defined over interval [0, 1]
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UniformDistribution.html">UniformDistribution</a> */
public class UniformDistribution extends AbstractContinuousDistribution implements Serializable {
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
        ? new DiracDistribution(clip.min())
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

  @Override // from PDF
  public Scalar at(Scalar x) {
    return clip.isInside(x) //
        ? clip.width().reciprocal()
        : RealScalar.ZERO;
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
