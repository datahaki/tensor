// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Gamma;
import ch.ethz.idsc.tensor.sca.Power;

/** GammaDistribution[1, lambda] == ExponentialDistribution[1 / lambda]
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/GammaDistribution.html">GammaDistribution</a> */
public class GammaDistribution implements Distribution, MeanInterface, PDF, VarianceInterface {
  /** @param alpha positive
   * @param beta positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessEquals(alpha, RealScalar.ZERO) || Scalars.lessEquals(beta, RealScalar.ZERO))
      throw TensorRuntimeException.of(alpha, beta);
    return new GammaDistribution(alpha, beta);
  }

  private final Scalar alpha;
  private final Scalar beta;
  private final Scalar factor;

  public GammaDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
    factor = Power.of(beta, alpha.negate()).divide(Gamma.of(alpha));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessEquals(x, RealScalar.ZERO))
      return RealScalar.ZERO;
    return Exp.of(x.negate().divide(beta)) //
        .multiply(Power.of(x, alpha.subtract(RealScalar.ONE))).multiply(factor);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return alpha.multiply(beta);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return alpha.multiply(beta).multiply(beta);
  }
}