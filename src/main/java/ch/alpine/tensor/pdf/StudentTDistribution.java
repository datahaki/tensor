// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Beta;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/StudentTDistribution.html">StudentTDistribution</a> */
public class StudentTDistribution implements Distribution, //
    MeanInterface, PDF, VarianceInterface, Serializable {
  /** @param mu
   * @param sigma positive
   * @param v positive
   * @return */
  public static Distribution of(Scalar mu, Scalar sigma, Scalar v) {
    return new StudentTDistribution( //
        mu, //
        Sign.requirePositive(sigma), //
        Sign.requirePositive(v));
  }

  /** @param mu
   * @param sigma
   * @param v
   * @return */
  public static Distribution of(Number mu, Number sigma, Number v) {
    return of(RealScalar.of(mu), RealScalar.of(sigma), RealScalar.of(v));
  }

  // ---
  private final Scalar mu;
  private final Scalar sigma;
  private final Scalar v;
  private final Scalar factor;
  private final ScalarUnaryOperator power;

  private StudentTDistribution(Scalar mu, Scalar sigma, Scalar v) {
    this.mu = mu;
    this.sigma = sigma;
    this.v = v;
    power = Power.function(RealScalar.ONE.add(v).divide(RealScalar.TWO));
    factor = Sqrt.FUNCTION.apply(v).multiply(sigma).multiply(Beta.of(v.multiply(RationalScalar.HALF), RationalScalar.HALF));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar f = x.subtract(mu).divide(sigma);
    return power.apply(v.divide(f.multiply(f).add(v))).divide(factor);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return Scalars.lessThan(RealScalar.ONE, v) //
        ? mu
        : DoubleScalar.INDETERMINATE;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return Scalars.lessThan(RealScalar.TWO, v) //
        ? v.multiply(sigma).multiply(sigma).divide(v.subtract(RealScalar.TWO))
        : DoubleScalar.INDETERMINATE;
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s, %s]", getClass().getSimpleName(), mu, sigma, v);
  }
}
