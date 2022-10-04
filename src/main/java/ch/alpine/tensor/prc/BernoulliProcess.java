// code by jph
package ch.alpine.tensor.prc;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;

/** Quote from Mathematica:
 * <blockquote>
 * BernoulliProcess at a fixed instant of time is a Bernoulli random variate with parameter p.
 * </blockquote>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BernoulliProcess.html">BernoulliProcess</a> */
public enum BernoulliProcess {
  ;
  /** @param p in the interval [0, 1]
   * @return */
  public static RandomProcess of(Scalar p) {
    return DiscreteTimeIidProcess.of(BernoulliDistribution.of(p));
  }

  /** @param p in the interval [0, 1]
   * @return */
  public static RandomProcess of(Number p) {
    return of(RealScalar.of(p));
  }
}
