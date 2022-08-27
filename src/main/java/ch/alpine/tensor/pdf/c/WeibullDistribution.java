// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.pow.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/WeibullDistribution.html">WeibullDistribution</a> */
public class WeibullDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param alpha strictly positive
   * @param beta strictly positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessThan(RealScalar.ZERO, alpha) && //
        Scalars.lessThan(RealScalar.ZERO, beta))
      return new WeibullDistribution(alpha, beta);
    throw new Throw(alpha, beta);
  }

  public static Distribution of(Number alpha, Number beta) {
    return of(RealScalar.of(alpha), RealScalar.of(beta));
  }

  // ---
  private final Scalar alpha;
  private final Scalar beta;
  private final Scalar ar;

  private WeibullDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
    ar = alpha.reciprocal();
  }

  @Override
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar x_b = x.divide(beta);
      Scalar t1 = Exp.FUNCTION.apply(Power.of(x_b, alpha).negate());
      Scalar t2 = Power.of(x_b, alpha.subtract(RealScalar.ONE));
      return Times.of(t1, t2, alpha).divide(beta);
    }
    return RealScalar.ZERO;
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    if (Scalars.lessThan(RealScalar.ZERO, x)) {
      Scalar x_b = x.divide(beta);
      Scalar t1 = Exp.FUNCTION.apply(Power.of(x_b, alpha).negate());
      return RealScalar.ONE.subtract(t1);
    }
    return RealScalar.ZERO;
  }

  @Override
  public Scalar mean() {
    return Gamma.FUNCTION.apply(RealScalar.ONE.add(ar)).multiply(beta);
  }

  @Override
  public Scalar variance() {
    Scalar t1 = Gamma.FUNCTION.apply(RealScalar.ONE.add(ar).add(ar));
    Scalar t2 = Gamma.FUNCTION.apply(RealScalar.ONE.add(ar));
    return t1.subtract(t2.multiply(t2)).multiply(beta).multiply(beta);
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    return Power.of(Log.FUNCTION.apply(RealScalar.ONE.subtract(p)).negate(), alpha.reciprocal()).multiply(beta);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("WeibullDistribution", alpha, beta);
  }
}
