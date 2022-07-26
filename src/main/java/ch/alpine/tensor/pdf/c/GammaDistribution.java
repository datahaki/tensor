// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.LogGamma;
import ch.alpine.tensor.sca.pow.Power;

/** special cases of the Gamma distribution are
 * <code>
 * GammaDistribution[1, lambda] == ExponentialDistribution[1 / lambda]
 * GammaDistribution[k, lambda] == ErlangDistribution[k, 1 / lambda] for k positive integer
 * </code>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/GammaDistribution.html">GammaDistribution</a> */
public class GammaDistribution implements Distribution, //
    MeanInterface, PDF, VarianceInterface, Serializable {
  /** @param alpha positive real
   * @param beta positive real
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessEquals(alpha, RealScalar.ZERO) || //
        Scalars.lessEquals(beta, RealScalar.ZERO))
      throw new Throw(alpha, beta);
    if (alpha.equals(RealScalar.ONE))
      return ExponentialDistribution.of(beta.reciprocal());
    return new GammaDistribution(alpha, beta);
  }

  // ---
  private final Scalar alpha;
  private final Scalar beta;
  private final Scalar factor;

  private GammaDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
    factor = Exp.FUNCTION.apply(Log.FUNCTION.apply(beta).multiply(alpha).add(LogGamma.FUNCTION.apply(alpha)).negate());
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x))
      return Exp.FUNCTION.apply(x.negate().divide(beta)) //
          .multiply(Power.of(x, alpha.subtract(RealScalar.ONE))).multiply(factor);
    return RealScalar.ZERO;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return alpha.multiply(beta);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return alpha.multiply(beta).multiply(beta);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("GammaDistribution", alpha, beta);
  }
}
