// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Sign;

/** Remark: the implementation of InverseCDF is not very accurate, expect errors of 1%.
 * 
 * Quote: "LogNormalDistribution is also known as the Galton distribution."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LogNormalDistribution.html">LogNormalDistribution</a> */
public class LogNormalDistribution implements ContinuousDistribution, Serializable {
  private static final Distribution STANDARD = LogNormalDistribution.of(RealScalar.ZERO, RealScalar.ONE);

  /** @param mu any real number
   * @param sigma any positive real number
   * @return instance of LogNormalDistribution
   * @throws Exception if sigma is zero or negative
   * @throws Exception if either parameter is of type {@link Quantity} */
  public static Distribution of(Scalar mu, Scalar sigma) {
    if (mu instanceof RealScalar && //
        sigma instanceof RealScalar)
      return new LogNormalDistribution(mu, sigma);
    throw TensorRuntimeException.of(mu, sigma);
  }

  /** @param mu any real number
   * @param sigma any positive real number
   * @return
   * @throws Exception if sigma is zero or negative */
  public static Distribution of(Number mu, Number sigma) {
    return new LogNormalDistribution(RealScalar.of(mu), RealScalar.of(sigma));
  }

  /** @return LogNormalDistribution[0, 1] */
  public static Distribution standard() {
    return STANDARD;
  }

  /***************************************************/
  private final Scalar mu;
  private final NormalDistribution normalDistribution;
  private final Scalar variance;

  private LogNormalDistribution(Scalar mu, Scalar sigma) {
    this.mu = mu;
    normalDistribution = (NormalDistribution) NormalDistribution.of(mu, sigma);
    variance = normalDistribution.variance();
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Sign.isPositive(x) //
        ? normalDistribution.p_lessThan(Log.FUNCTION.apply(x))
        : RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Sign.isPositive(x) //
        ? normalDistribution.at(Log.FUNCTION.apply(x)).divide(x)
        : RealScalar.ZERO;
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return Exp.FUNCTION.apply(normalDistribution.randomVariate(random));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return Exp.FUNCTION.apply(variance.multiply(RationalScalar.HALF).add(mu));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return Exp.FUNCTION.apply(variance.add(mu).add(mu)).multiply( //
        Exp.FUNCTION.apply(variance).subtract(RealScalar.ONE));
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return Exp.FUNCTION.apply(normalDistribution.quantile(p));
  }

  @Override // from Object
  public String toString() {
    return "Log" + normalDistribution.toString();
  }
}
