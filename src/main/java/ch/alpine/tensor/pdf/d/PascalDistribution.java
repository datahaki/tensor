// code by jph
package ch.alpine.tensor.pdf.d;

import java.math.BigInteger;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;

/** Careful:
 * when p is specified in exact precision then the PDF evaluated at great integers
 * results in increasingly complex expressions.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PascalDistribution.html">PascalDistribution</a> */
public class PascalDistribution extends EvaluatedDiscreteDistribution {
  /** @param n positive number of successes
   * @param p success probability in the unit interval [0, 1]
   * @return distribution of the number of trials with success probability p before n successes occur
   * @throws Exception if n is negative or zero, or p is outside the unit interval */
  public static Distribution of(int n, Scalar p) {
    return new PascalDistribution(Integers.requirePositive(n), Clips.unit().requireInside(p));
  }

  /** @param n positive number of successes
   * @param p success probability in the unit interval [0, 1]
   * @return distribution of the number of trials with success probability p before n successes occur */
  public static Distribution of(int n, Number p) {
    return of(n, RealScalar.of(p));
  }

  // ---
  private final int n;
  private final Scalar p;
  private final Scalar _1_p;
  private final Scalar pn;

  private PascalDistribution(int n, Scalar p) {
    this.n = n;
    this.p = p;
    _1_p = RealScalar.ONE.subtract(p);
    pn = Power.of(p, n);
    build(Chop._14);
  }

  @Override
  public Clip support() {
    return Clips.interval(RealScalar.of(n), DoubleScalar.POSITIVE_INFINITY);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(n).divide(p);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return mean().multiply(_1_p).divide(p);
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger x) { // lowerBound() <= x
    Scalar factor = Power.of(_1_p, RealScalar.of(x).add(RealScalar.of(-n)));
    return Scalars.isZero(factor) //
        ? RealScalar.ZERO
        : factor.multiply(pn).multiply(Binomial.of(x.intValueExact() - 1, n - 1));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("PascalDistribution", n, p);
  }
}
