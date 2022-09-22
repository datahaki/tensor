// code by jph
package ch.alpine.tensor.pdf.d;

import java.io.Serializable;
import java.math.BigInteger;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.FindInteger;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.gam.LogGamma;

/** The cdf grows very slowly.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/WaringYuleDistribution.html">WaringYuleDistribution</a> */
public class WaringYuleDistribution extends AbstractDiscreteDistribution implements Serializable {
  /** @param alpha positive
   * @param beta positive
   * @return */
  public static Distribution of(Scalar alpha, Scalar beta) {
    if (Scalars.lessThan(RealScalar.ZERO, alpha) && //
        Scalars.lessThan(RealScalar.ZERO, beta))
      return new WaringYuleDistribution(alpha, beta);
    throw new Throw(alpha, beta);
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
  private final Scalar lgp;

  private WaringYuleDistribution(Scalar alpha, Scalar beta) {
    this.alpha = alpha;
    this.beta = beta;
    lgp = LogGamma.FUNCTION.apply(alpha.add(beta)).subtract(LogGamma.FUNCTION.apply(beta));
  }

  @Override // from DiscreteDistribution
  public BigInteger lowerBound() {
    return BigInteger.ZERO;
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger x) {
    Scalar sum = beta.add(RealScalar.of(x));
    Scalar t1 = LogGamma.FUNCTION.apply(sum);
    Scalar t2 = LogGamma.FUNCTION.apply(alpha.add(sum).add(RealScalar.ONE));
    return Exp.FUNCTION.apply(lgp.add(t1).subtract(t2)).multiply(alpha);
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    // TODO TENSOR REFACTOR pattern exist somewhere else!?
    return Scalars.lessThan(RealScalar.ZERO, x) //
        ? private_cdf(Ceiling.FUNCTION.apply(x.subtract(RealScalar.ONE)))
        : RealScalar.ZERO;
  }

  @Override
  public Scalar p_lessEquals(Scalar x) {
    return Scalars.lessEquals(RealScalar.ZERO, x) //
        ? private_cdf(Floor.FUNCTION.apply(x))
        : RealScalar.ZERO;
  }

  private Scalar private_cdf(Scalar x) {
    Scalar sum = beta.add(x).add(RealScalar.ONE);
    Scalar t1 = LogGamma.FUNCTION.apply(sum);
    Scalar t2 = LogGamma.FUNCTION.apply(alpha.add(sum));
    return RealScalar.ONE.subtract(Exp.FUNCTION.apply(lgp.add(t1).subtract(t2)));
  }

  @Override
  public Scalar quantile(Scalar p) {
    if (p.equals(RealScalar.ONE))
      return DoubleScalar.POSITIVE_INFINITY;
    return protected_quantile(p);
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    // "For a discrete distribution dist the inverse CDF at p is the smallest integer x such that CDF[dist,x] >= p."
    // FIXME
    return FindInteger.min(x -> Scalars.lessEquals(p, p_lessEquals(x)), Clips.interval(lowerBound(), Integer.MAX_VALUE));
  }

  @Override
  public Scalar mean() {
    return Scalars.lessThan(RealScalar.ONE, alpha) //
        ? alpha.subtract(RealScalar.ONE).reciprocal()
        : DoubleScalar.INDETERMINATE;
  }

  @Override
  public Scalar variance() {
    // requires Hypergeometric2F1Regularized
    throw new UnsupportedOperationException();
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("WaringYuleDistribution", alpha, beta);
  }
}
