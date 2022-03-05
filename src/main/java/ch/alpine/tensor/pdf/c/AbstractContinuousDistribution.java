// code by jph
package ch.alpine.tensor.pdf.c;

import java.util.Random;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.sca.Clips;

public abstract class AbstractContinuousDistribution implements UnivariateDistribution {
  @Override // from CDF
  public final Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from InverseCDF
  public final Scalar quantile(Scalar p) {
    return protected_quantile(Clips.unit().requireInside(p));
  }

  @Override // from RandomVariateInterface
  public final Scalar randomVariate(Random random) {
    // {@link Random#nextDouble()} samples uniformly from the range 0.0 (inclusive) to 1.0d (exclusive)
    return protected_quantile(DoubleScalar.of(random.nextDouble()));
  }

  /** @param p guaranteed to be inside the interval [0, 1]
   * @return */
  protected abstract Scalar protected_quantile(Scalar p);
}
