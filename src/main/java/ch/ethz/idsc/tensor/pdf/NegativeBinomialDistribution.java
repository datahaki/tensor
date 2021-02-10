// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Binomial;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/NegativeBinomialDistribution.html">NegativeBinomialDistribution</a> */
public class NegativeBinomialDistribution extends EvaluatedDiscreteDistribution implements VarianceInterface {
  private static final long serialVersionUID = -4979399049893680328L;
  private static final Chop TOLERANCE = Chop._08;

  /** @param n
   * @param p
   * @return */
  public static Distribution of(int n, Scalar p) {
    return new NegativeBinomialDistribution( //
        Integers.requirePositiveOrZero(n), //
        Clips.unit().requireInside(p));
  }

  /***************************************************/
  private final int n;
  private final Scalar p;
  private final Scalar _1_p;
  private final Scalar pn;

  private NegativeBinomialDistribution(int n, Scalar p) {
    this.n = n;
    this.p = Sign.requirePositive(p);
    _1_p = RealScalar.ONE.subtract(p);
    pn = Power.of(p, n);
    inverse_cdf_build(TOLERANCE);
  }

  @Override
  protected Scalar protected_p_equals(int k) {
    return pn.multiply(Power.of(_1_p, k)).multiply(Binomial.of(n - 1 + k, n - 1));
  }

  @Override // from DiscreteDistribution
  public int lowerBound() {
    return 0;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(n).multiply(_1_p).divide(p);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return mean().divide(p);
  }
}
