// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Beta;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BetaDistribution.html">BetaDistribution</a> */
public class BetaDistribution implements Distribution, MeanInterface, PDF, VarianceInterface, Serializable {
  /** @param alpha positive
   * @param beta positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessEquals(alpha, RealScalar.ZERO) || //
        Scalars.lessEquals(beta, RealScalar.ZERO))
      throw TensorRuntimeException.of(alpha, beta);
    // LONGTERM for beta == 1 OR alpha == 1 the distribution does not require the beta function
    return new BetaDistribution(alpha, beta);
  }

  /** @param alpha positive
   * @param beta positive
   * @return */
  public static Distribution of(Number alpha, Number beta) {
    return of(RealScalar.of(alpha), RealScalar.of(beta));
  }

  /***************************************************/
  private final Scalar alpha;
  private final Scalar beta;
  private final Scalar factor;
  private final ScalarUnaryOperator power_a;
  private final ScalarUnaryOperator power_b;

  private BetaDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
    power_a = Power.function(alpha.subtract(RealScalar.ONE));
    power_b = Power.function(beta.subtract(RealScalar.ONE));
    factor = Beta.of(alpha, beta);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Scalars.lessThan(RealScalar.ZERO, x) //
        && Scalars.lessThan(x, RealScalar.ONE) //
            ? power_a.apply(x).multiply(power_b.apply(RealScalar.ONE.subtract(x))).divide(factor)
            : RealScalar.ZERO;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return alpha.divide(alpha.add(beta));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    Scalar a_b = alpha.add(beta);
    return alpha.divide(a_b).multiply(beta).divide(a_b).divide(RealScalar.ONE.add(a_b));
  }
}
