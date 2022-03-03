// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ParetoDistribution.html">ParetoDistribution</a> */
public class ParetoDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param k strictly positive real number
   * @param alpha strictly positive real number
   * @return */
  public static Distribution of(Scalar k, Scalar alpha) {
    if (Scalars.lessThan(RealScalar.ZERO, k))
      return new ParetoDistribution(k, Sign.requirePositive(alpha));
    throw TensorRuntimeException.of(k);
  }

  /** @param k strictly positive real number
   * @param alpha strictly positive real number
   * @return */
  public static Distribution of(Number k, Number alpha) {
    return of(RealScalar.of(k), RealScalar.of(alpha));
  }

  // ---
  private final Scalar k;
  private final Scalar alpha;
  private final Scalar k_alpha;

  private ParetoDistribution(Scalar k, Scalar alpha) {
    this.k = k;
    this.alpha = alpha;
    k_alpha = Power.of(k, alpha);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Scalars.lessEquals(k, x) //
        ? RealScalar.ONE.subtract(Power.of(k.divide(x), alpha))
        : x.one().zero();
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Scalars.lessEquals(k, x) //
        ? k_alpha.divide(Power.of(x, alpha.add(RealScalar.ONE))).multiply(alpha)
        : x.one().zero();
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return Scalars.lessThan(RealScalar.ONE, alpha) //
        ? k.multiply(alpha).divide(alpha.subtract(RealScalar.ONE))
        : DoubleScalar.INDETERMINATE;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    if (Scalars.lessThan(RealScalar.TWO, alpha)) {
      Scalar amo = alpha.subtract(RealScalar.ONE);
      return k.multiply(k).multiply(alpha).divide(alpha.subtract(RealScalar.TWO).multiply(amo).multiply(amo));
    }
    return DoubleScalar.INDETERMINATE;
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    return k.divide(Power.of(RealScalar.ONE.subtract(p), alpha.reciprocal()));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s]", getClass().getSimpleName(), k, alpha);
  }
}
