// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Sqrt;

/** "MaxwellDistribution is also known as Maxwell-Boltzmann distribution."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MaxwellDistribution.html">MaxwellDistribution</a> */
public class MaxwellDistribution implements Distribution, CDF, PDF, MeanInterface, VarianceInterface, Serializable {
  private static final Scalar VAR = RealScalar.of(3).subtract(RealScalar.of(8).divide(Pi.VALUE));
  private static final Scalar SQRT_2 = Sqrt.FUNCTION.apply(RealScalar.TWO);
  private static final Scalar SQRT_PI_2 = Sqrt.FUNCTION.apply(Pi.HALF);

  /** @param sigma positive
   * @return */
  public static Distribution of(Scalar sigma) {
    if (Scalars.lessThan(RealScalar.ZERO, sigma))
      return new MaxwellDistribution(sigma);
    throw TensorRuntimeException.of(sigma);
  }

  /** @param sigma positive
   * @return */
  public static Distribution of(Number sigma) {
    return of(RealScalar.of(sigma));
  }

  // ---
  private final Scalar sigma;
  private final Scalar s2;
  private final Scalar s2_n2;
  private final Scalar s3;

  private MaxwellDistribution(Scalar sigma) {
    this.sigma = sigma;
    s2 = sigma.multiply(sigma);
    s2_n2 = s2.add(s2).negate();
    s3 = s2.multiply(sigma);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Scalars.lessThan(x, RealScalar.ZERO))
      return RealScalar.ZERO;
    Scalar x2 = x.multiply(x);
    return Exp.FUNCTION.apply(x2.divide(s2_n2)).divide(SQRT_PI_2).multiply(x2).divide(s3);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    if (Scalars.lessThan(x, RealScalar.ZERO))
      return RealScalar.ZERO;
    return Erf.FUNCTION.apply(x.divide(SQRT_2).divide(sigma)).subtract( //
        Exp.FUNCTION.apply(x.multiply(x).divide(s2_n2)).divide(SQRT_PI_2).multiply(x).divide(sigma));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    Scalar scalar = sigma.divide(SQRT_PI_2);
    return scalar.add(scalar);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return s2.multiply(VAR);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), sigma);
  }
}
