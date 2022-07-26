// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.pow.Power;

/** Hint:
 * The InverseCDF of a FrechetDistribution at p == 1 is not defined.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FrechetDistribution.html">FrechetDistribution</a> */
public class FrechetDistribution implements UnivariateDistribution, Serializable {
  private static final double NEXT_DOWN_ONE = Math.nextDown(1.0);

  /** @param alpha positive real
   * @param beta positive, may be instance of {@link Quantity}
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessEquals(alpha, RealScalar.ZERO))
      throw new Throw(alpha);
    return new FrechetDistribution(alpha, Sign.requirePositive(beta));
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

  private FrechetDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return randomVariate(random.nextDouble());
  }

  @PackageTestAccess
  Scalar randomVariate(double reference) {
    // avoid result -Infinity when reference is close to 1.0
    double uniform = reference == NEXT_DOWN_ONE //
        ? reference
        : Math.nextUp(reference);
    return protected_quantile(DoubleScalar.of(uniform));
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return protected_quantile(Clips.unit().requireInside(p));
  }

  protected Scalar protected_quantile(Scalar p) {
    return beta.multiply(Power.of(Log.FUNCTION.apply(p).negate(), alpha.reciprocal().negate()));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return Scalars.lessEquals(alpha, RealScalar.ONE) //
        ? beta.multiply(DoubleScalar.POSITIVE_INFINITY)
        : beta.multiply(Gamma.FUNCTION.apply(RealScalar.ONE.subtract(alpha.reciprocal())));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    if (Scalars.lessEquals(alpha, RealScalar.TWO))
      return beta.multiply(beta).multiply(DoubleScalar.POSITIVE_INFINITY);
    Scalar term = Gamma.FUNCTION.apply(RealScalar.ONE.subtract(RealScalar.TWO.divide(alpha)));
    return beta.multiply(beta).multiply(term).subtract(AbsSquared.FUNCTION.apply(mean()));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar factor = Power.of(x.divide(beta), RealScalar.ONE.add(alpha).negate());
    return p_lessThan(x).multiply(alpha).multiply(factor).divide(beta);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Sign.isPositive(x) //
        ? Exp.FUNCTION.apply(Power.of(x.divide(beta), alpha.negate()).negate())
        : RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("FrechetDistribution", alpha, beta);
  }
}
