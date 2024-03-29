// code by jph
package ch.alpine.tensor.pdf.d;

import java.math.BigInteger;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;

/** Quote: "the distribution of the number of failures in a sequence of trials with success
 * probability p before n successes occur."
 * 
 * Careful:
 * when p is specified in exact precision then the PDF evaluated at great integers
 * results in increasingly complex expressions.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/NegativeBinomialDistribution.html">NegativeBinomialDistribution</a> */
public class NegativeBinomialDistribution extends EvaluatedDiscreteDistribution {
  /** @param n non-negative
   * @param p in the interval (0, 1]
   * @return */
  public static Distribution of(int n, Scalar p) {
    return new NegativeBinomialDistribution( //
        Integers.requirePositiveOrZero(n), //
        Clips.unit().requireInside(p));
  }

  /** @param n non-negative
   * @param p in the interval (0, 1]
   * @return */
  public static Distribution of(int n, Number p) {
    return of(n, RealScalar.of(p));
  }

  // ---
  private final int n;
  private final Scalar p;
  private final Scalar _1_p;
  private final Scalar pn;

  private NegativeBinomialDistribution(int n, Scalar p) {
    this.n = n;
    this.p = Sign.requirePositive(p);
    _1_p = RealScalar.ONE.subtract(p);
    pn = Power.of(p, n);
    build(Tolerance.CHOP);
  }

  @Override // from DiscreteDistribution
  public BigInteger lowerBound() {
    return BigInteger.ZERO;
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger x) {
    Scalar factor = Power.of(_1_p, x);
    return Scalars.isZero(factor) //
        ? RealScalar.ZERO
        : pn.multiply(factor).multiply(Binomial.of(n - 1 + x.intValueExact(), n - 1));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(n).multiply(_1_p).divide(p);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return mean().divide(p);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("NegativeBinomialDistribution", n, p);
  }
}
