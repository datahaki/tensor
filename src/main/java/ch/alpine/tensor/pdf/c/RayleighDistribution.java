// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RayleighDistribution.html">RayleighDistribution</a> */
public class RayleighDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param sigma positive real
   * @return */
  public static Distribution of(Scalar sigma) {
    if (Scalars.lessThan(RealScalar.ZERO, sigma))
      return new RayleighDistribution(sigma);
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

  private RayleighDistribution(Scalar sigma) {
    this.sigma = sigma;
    s2 = sigma.multiply(sigma);
    s2_n2 = s2.add(s2).negate();
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return Scalars.lessThan(x, RealScalar.ZERO) //
        ? x.zero()
        : Exp.FUNCTION.apply(x.multiply(x).divide(s2_n2)).multiply(x).divide(s2);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Scalars.lessThan(x, RealScalar.ZERO) //
        ? x.zero()
        : RealScalar.ONE.subtract(Exp.FUNCTION.apply(x.multiply(x).divide(s2_n2)));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return sigma.multiply(Sqrt.FUNCTION.apply(Pi.HALF));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return RealScalar.TWO.subtract(Pi.HALF).multiply(s2);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    Scalar _1_p = RealScalar.ONE.subtract(p);
    return Sqrt.FUNCTION.apply(Log.FUNCTION.apply(_1_p.multiply(_1_p)).negate()).multiply(sigma);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("RayleighDistribution", sigma);
  }
}
