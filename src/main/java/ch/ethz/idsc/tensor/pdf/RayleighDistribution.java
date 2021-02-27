// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RayleighDistribution.html">RayleighDistribution</a> */
public class RayleighDistribution extends AbstractContinuousDistribution implements //
    InverseCDF, MeanInterface, VarianceInterface, Serializable {
  private static final long serialVersionUID = 363894251003495820L;

  /** @param sigma positive
   * @return */
  public static Distribution of(Scalar sigma) {
    if (Scalars.lessThan(RealScalar.ZERO, sigma))
      return new RayleighDistribution(sigma);
    throw TensorRuntimeException.of(sigma);
  }

  /** @param sigma positive
   * @return */
  public static Distribution of(Number sigma) {
    return of(RealScalar.of(sigma));
  }

  /***************************************************/
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
        ? RealScalar.ZERO
        : Exp.FUNCTION.apply(x.multiply(x).divide(s2_n2)).multiply(x).divide(s2);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return Scalars.lessThan(x, RealScalar.ZERO) //
        ? RealScalar.ZERO
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

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return _quantile(Clips.unit().requireInside(p));
  }

  private Scalar _quantile(Scalar p) {
    Scalar _1_p = RealScalar.ONE.subtract(p);
    return Sqrt.FUNCTION.apply(Log.FUNCTION.apply(_1_p.multiply(_1_p)).negate()).multiply(sigma);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar randomVariate(double reference) {
    // {@link Random#nextDouble()} samples uniformly from the range 0.0 (inclusive) to 1.0d (exclusive)
    return _quantile(RealScalar.of(reference));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), sigma);
  }
}
