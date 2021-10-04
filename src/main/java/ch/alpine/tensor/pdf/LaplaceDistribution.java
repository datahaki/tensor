// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LaplaceDistribution.html">LaplaceDistribution</a> */
public class LaplaceDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param mean
   * @param beta positive
   * @return */
  public static Distribution of(Scalar mean, Scalar beta) {
    Scalars.compare(mean, beta); // assert that parameters have identical units
    return new LaplaceDistribution(mean, Sign.requirePositive(beta));
  }

  /** @param mean
   * @param beta positive
   * @return */
  public static Distribution of(Number mean, Number beta) {
    return of(RealScalar.of(mean), RealScalar.of(beta));
  }

  // ---
  private final Scalar mean;
  private final Scalar beta;

  private LaplaceDistribution(Scalar mean, Scalar beta) {
    this.mean = mean;
    this.beta = beta;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Exp.FUNCTION.apply(Abs.between(x, mean).negate().divide(beta)).divide(beta.add(beta));
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar p = Exp.FUNCTION.apply(Abs.between(x, mean).negate().divide(beta)).multiply(RationalScalar.HALF);
    return Scalars.lessEquals(mean, x) //
        ? RealScalar.ONE.subtract(p)
        : p;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return mean;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    Scalar b2 = beta.multiply(beta);
    return b2.add(b2);
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    if (Scalars.lessEquals(p, RationalScalar.HALF))
      return mean.add(Log.FUNCTION.apply(p.add(p)).multiply(beta));
    Scalar c = RealScalar.ONE.subtract(p);
    return mean.subtract(Log.FUNCTION.apply(c.add(c)).multiply(beta));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s]", getClass().getSimpleName(), mean, beta);
  }
}
