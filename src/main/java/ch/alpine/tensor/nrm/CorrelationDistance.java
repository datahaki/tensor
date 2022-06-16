// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Mean;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CorrelationDistance.html">CorrelationDistance</a> */
public enum CorrelationDistance {
  ;
  /** @param u vector
   * @param v vector
   * @return */
  public static Scalar of(Tensor u, Tensor v) {
    return CosineDistance.of( //
        u.map(Mean.of(u).negate()::add), //
        v.map(Mean.of(v).negate()::add));
  }
}
