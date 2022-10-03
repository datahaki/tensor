// code by jph
package ch.alpine.tensor.prc;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PoissonProcess.html">PoissonProcess</a> */
public enum PoissonProcess {
  ;
  public static RandomProcess of(Scalar lambda) {
    return RenewalProcess.of(ExponentialDistribution.of(lambda));
  }
}
