// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.pow.Power;

public class InverseGammaDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param alpha positive
   * @param beta positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (alpha.equals(RationalScalar.HALF))
      return LevyDistribution.of(RealScalar.ZERO, beta.add(beta));
    return new InverseGammaDistribution( //
        Sign.requirePositive(alpha), //
        Sign.requirePositive(beta));
  }

  /** @param alpha positive
   * @param beta positive
   * @return */
  public static Distribution of(Number alpha, Number beta) {
    return of(RealScalar.of(alpha), RealScalar.of(beta));
  }

  // ---
  private final Scalar alpha;
  private final Scalar beta;
  private ScalarUnaryOperator power;
  private Scalar gamma;

  private InverseGammaDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
    power = Power.function(alpha);
    gamma = Gamma.FUNCTION.apply(alpha);
  }

  @Override
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar f = beta.divide(x);
      return Exp.FUNCTION.apply(f.negate()).divide(x).multiply(power.apply(f)).divide(gamma);
    }
    return RealScalar.ZERO;
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    throw new Throw(x);
  }

  @Override
  public Scalar mean() {
    return Scalars.lessThan(RealScalar.ONE, alpha) //
        ? beta.divide(alpha.subtract(RealScalar.ONE))
        : DoubleScalar.INDETERMINATE;
  }

  @Override
  public Scalar variance() {
    if (Scalars.lessThan(RealScalar.TWO, alpha)) {
      Scalar f1 = alpha.subtract(RealScalar.ONE);
      return beta.multiply(beta).divide(f1.multiply(f1)).divide(alpha.subtract(RealScalar.TWO));
    }
    return DoubleScalar.INDETERMINATE;
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    throw new Throw(p);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("InverseGammaDistribution", alpha, beta);
  }
}
