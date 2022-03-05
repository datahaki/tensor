// code by jph
package ch.alpine.tensor.pdf.d;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;

/** inspired by
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
  private final Scalar o_p;

  private PascalDistribution(int n, Scalar p) {
    this.n = n;
    this.p = p;
    o_p = RealScalar.ONE.subtract(p);
    build(Chop._14);
  }

  @Override // from DiscreteDistribution
  public int lowerBound() {
    return n;
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return RealScalar.of(n).divide(p);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return mean().multiply(o_p).divide(p);
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(int x) { // lowerBound() <= x
    return Power.of(o_p, x - n).multiply(Power.of(p, n)).multiply(Binomial.of(x - 1, n - 1));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%d, %s]", getClass().getSimpleName(), n, p);
  }
}
