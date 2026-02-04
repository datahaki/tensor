// code by jph
package ch.alpine.tensor.pdf.c;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.sca.Clips;

/** for a continuous probability distribution over the real numbers
 * that does not have a discontinuity over the support */
public abstract class AbstractContinuousDistribution implements UnivariateDistribution {
  @Override // from CDF
  public final Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from InverseCDF
  public final Scalar quantile(Scalar p) {
    return protected_quantile(Clips.unit().requireInside(p));
  }

  /** @param p guaranteed to be inside the interval [0, 1]
   * @return */
  protected abstract Scalar protected_quantile(Scalar p);

  /* non-final only because FrechetDistribution, ErlangDistribution, etc. require override */
  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return protected_quantile(DoubleScalar.of(randomGenerator.nextDouble()));
  }
}
