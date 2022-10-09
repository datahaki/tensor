// code by jph
package ch.alpine.tensor.prc;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.qty.Quantity;

/** Quote from Mathematica:
 * <blockquote>
 * The times between events are independent and follow ExponentialDistribution[lambda].
 * </blockquote>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PoissonProcess.html">PoissonProcess</a>
 * 
 * @see ExponentialDistribution */
public enum PoissonProcess {
  ;
  /** @param lambda positive, may be instance of {@link Quantity}
   * @return */
  public static RandomProcess of(Scalar lambda) {
    return RenewalProcess.of(ExponentialDistribution.of(lambda));
  }

  /** @param lambda positive
   * @return */
  public static RandomProcess of(Number lambda) {
    return of(RealScalar.of(lambda));
  }
}
