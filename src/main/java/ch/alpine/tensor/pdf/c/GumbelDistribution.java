// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Gamma;

/** Quantile returns -Infinity as reference approaches 0.0
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/GumbelDistribution.html">GumbelDistribution</a> */
public class GumbelDistribution extends AbstractContinuousDistribution implements Serializable {
  private static final Scalar PISQUARED_6 = DoubleScalar.of(1.644934066848226436472415166646);

  /** parameters may be instance of {@link Quantity} with identical units
   * 
   * @param alpha any real number
   * @param beta positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    Scalars.compare(alpha, alpha.add(beta)); // assert that parameters have identical unit
    return new GumbelDistribution(alpha, Sign.requirePositive(beta));
  }

  /** @param alpha any real number
   * @param beta positive
   * @return */
  public static Distribution of(Number alpha, Number beta) {
    return of(RealScalar.of(alpha), RealScalar.of(beta));
  }

  // ---
  private final Scalar alpha;
  private final Scalar beta;

  private GumbelDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return RealScalar.ONE.subtract(Exp.FUNCTION.apply( //
        Exp.FUNCTION.apply(x.subtract(alpha).divide(beta)).negate()));
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    return alpha.add(beta.multiply(Log.FUNCTION.apply(Log.FUNCTION.apply(RealScalar.ONE.subtract(p)).negate())));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return alpha.subtract(Gamma.EULER.multiply(beta));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return PISQUARED_6.multiply(beta).multiply(beta);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar map = x.subtract(alpha).divide(beta);
    return Exp.FUNCTION.apply(map.subtract(Exp.FUNCTION.apply(map))).divide(beta);
  }

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("GumbelDistribution", alpha, beta);
  }
}
