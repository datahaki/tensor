// code by jph
package ch.alpine.tensor.pdf.d;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.LogGamma;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BorelTannerDistribution.html">BorelTannerDistribution</a> */
public class BorelTannerDistribution extends EvaluatedDiscreteDistribution {
  /** @param alpha inside open interval (0, 1)
   * @param n strictly positive
   * @return */
  public static Distribution of(Scalar alpha, int n) {
    if (Scalars.lessThan(RealScalar.ZERO, alpha) && //
        Scalars.lessThan(alpha, RealScalar.ONE))
      return new BorelTannerDistribution(alpha, Integers.requirePositive(n));
    throw new Throw(alpha);
  }

  /** @param alpha inside open interval (0, 1)
   * @param n strictly positive
   * @return */
  public static Distribution of(Number alpha, int n) {
    return of(RealScalar.of(alpha), n);
  }

  // ---
  private final Scalar alpha;
  private final Scalar n;
  private final Scalar o_alpha;
  private final Scalar logAlpha;
  private final int _n;

  private BorelTannerDistribution(Scalar alpha, int n) {
    this.alpha = alpha;
    this.n = RealScalar.of(n);
    o_alpha = RealScalar.ONE.subtract(alpha);
    logAlpha = Log.FUNCTION.apply(alpha);
    _n = n;
    build(Tolerance.CHOP);
  }

  @Override // from DiscreteDistribution
  public int lowerBound() {
    return _n;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return n.divide(o_alpha);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return mean().multiply(alpha).divide(o_alpha).divide(o_alpha);
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(int _x) {
    Scalar x = RealScalar.of(_x);
    Scalar x_n = x.subtract(n);
    Scalar s0 = LogGamma.FUNCTION.apply(x_n.add(RealScalar.ONE)).negate();
    Scalar s1 = x.multiply(alpha).negate();
    Scalar s2 = Log.FUNCTION.apply(x).multiply(x_n.subtract(RealScalar.ONE));
    Scalar s3 = logAlpha.multiply(x_n);
    return n.multiply(Exp.FUNCTION.apply(s0.add(s1).add(s2).add(s3)));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("BorelTannerDistribution", alpha, _n);
  }
}
