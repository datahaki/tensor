// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.itp.BernsteinBasis;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clips;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BinomialDistribution.html">BinomialDistribution</a>
 * 
 * @see BinomialRandomVariate */
public class BinomialDistribution extends EvaluatedDiscreteDistribution implements VarianceInterface {
  private static final long serialVersionUID = -1987644165670884763L;

  /** Example:
   * PDF[BinomialDistribution[10, 1/3], 1] == 5120/59049
   * 
   * <p>For some input parameters (n, p), the computation of the exact PDF can be challenging:
   * Extreme cases are
   * BinomialDistribution[10000, 0.5] <- several probabilities are below machine precision (~10^-300)
   * BinomialDistribution[10000, 11/13] <- probabilities are complicated integer fractions
   * 
   * @param n non-negative
   * @param p in the interval [0, 1]
   * @return an instance of {@link BinomialDistribution} if the CDF could be computed correctly,
   * otherwise an instance of {@link BinomialRandomVariate}, which has the capability to
   * generate random variates, but is neither PDF, or CDF. */
  public static Distribution of(int n, Scalar p) {
    Tensor table = BernsteinBasis.of(n, Clips.unit().requireInside(p));
    return Tolerance.CHOP.isClose(Total.of(table), RealScalar.ONE) //
        ? new BinomialDistribution(n, p, table) //
        : new BinomialRandomVariate(n, p);
  }

  /** @param n non-negative integer
   * @param p in the interval [0, 1]
   * @return */
  public static Distribution of(int n, Number p) {
    return of(n, RealScalar.of(p));
  }

  /***************************************************/
  private final int n;
  private final Scalar p;
  private final Tensor table;

  private BinomialDistribution(int n, Scalar p, Tensor table) {
    this.n = n;
    this.p = p;
    this.table = table;
    inverse_cdf_build(n);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(n).multiply(p);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return mean().multiply(RealScalar.ONE.subtract(p));
  }

  @Override // from DiscreteDistribution
  public int lowerBound() {
    return 0;
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(int k) {
    return k <= n //
        ? table.Get(k)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%d, %s]", getClass().getSimpleName(), n, p);
  }
}
