// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.opt.fnd.FindRoot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.erf.Erf;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Sqrt;

/** "MaxwellDistribution is also known as Maxwell-Boltzmann distribution."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MaxwellDistribution.html">MaxwellDistribution</a> */
public class MaxwellDistribution extends AbstractContinuousDistribution implements Serializable {
  private static final Scalar VAR = RealScalar.of(3).subtract(RealScalar.of(8).divide(Pi.VALUE));
  private static final Scalar SQRT_2 = Sqrt.FUNCTION.apply(RealScalar.TWO);
  private static final Scalar SQRT_PI_2 = Sqrt.FUNCTION.apply(Pi.HALF);

  /** @param sigma positive real scalar
   * @return */
  public static Distribution of(Scalar sigma) {
    if (Scalars.lessThan(RealScalar.ZERO, sigma))
      return new MaxwellDistribution(sigma);
    throw new Throw(sigma);
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
  private final Scalar mean;

  private MaxwellDistribution(Scalar sigma) {
    this.sigma = sigma;
    s2 = sigma.multiply(sigma);
    s2_n2 = s2.add(s2).negate();
    s3 = s2.multiply(sigma);
    Scalar scalar = sigma.divide(SQRT_PI_2);
    mean = scalar.add(scalar);
  }

  @Override // from UnivariateDistribution
  public Clip support() {
    return Clips.positive(DoubleScalar.POSITIVE_INFINITY);
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

  @Override // from AbstractContinuousDistribution
  public Scalar protected_quantile(Scalar p) {
    return FindRoot.of(x -> p_lessThan(x).subtract(p)).above(mean.zero(), mean);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return mean;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return s2.multiply(VAR);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("MaxwellDistribution", sigma);
  }
}
