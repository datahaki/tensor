// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.erf.Erfc;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/InverseGaussianDistribution.html">InverseGaussianDistribution</a> */
public class InverseGaussianDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param mu positive
   * @param lambda positive
   * @return */
  public static Distribution of(Scalar mu, Scalar lambda) {
    return new InverseGaussianDistribution( //
        Sign.requirePositive(mu), //
        Sign.requirePositive(lambda));
  }

  /** @param mu positive
   * @param lambda positive
   * @return */
  public static Distribution of(Number mu, Number lambda) {
    return of(RealScalar.of(mu), RealScalar.of(lambda));
  }

  // ---
  private final Scalar mu;
  private final Scalar lambda;

  private InverseGaussianDistribution(Scalar mu, Scalar lambda) {
    this.mu = mu;
    this.lambda = lambda;
  }

  @Override
  public Scalar at(Scalar x) {
    Scalar d = x.subtract(mu);
    if (Sign.isPositive(x)) {
      Scalar exp = Times.of(d, d, lambda).divide(Times.of(x.add(x), mu, mu)).negate();
      return Exp.FUNCTION.apply(exp).multiply(Sqrt.FUNCTION.apply(lambda.divide(x))).divide(Sqrt.FUNCTION.apply(Pi.TWO).multiply(x));
    }
    return lambda.reciprocal().zero();
  }

  @Override
  public Scalar p_lessThan(Scalar x) {
    Scalar re = Sqrt.FUNCTION.apply(lambda.divide(x.add(x))).divide(mu);
    Scalar p1 = Erfc.FUNCTION.apply(re.multiply(mu.subtract(x)));
    Scalar p2 = Erfc.FUNCTION.apply(re.multiply(mu.add(x)));
    return p1.add(Exp.FUNCTION.apply(lambda.add(lambda).divide(mu)).multiply(p2)).multiply(RationalScalar.HALF);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return mu;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return Power.of(mu, 3).divide(lambda);
  }

  @Override
  protected Scalar protected_quantile(Scalar p) {
    throw new Throw(mu, lambda);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("InverseGaussianDistribution", mu, lambda);
  }
}
