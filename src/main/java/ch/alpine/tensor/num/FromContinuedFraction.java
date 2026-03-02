// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Reverse;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FromContinuedFraction.html">FromContinuedFraction</a> */
public enum FromContinuedFraction {
  ;
  /** @param coeffs
   * @return */
  public static Scalar of(Tensor coeffs) {
    coeffs = Reverse.of(coeffs);
    Scalar x = coeffs.Get(0);
    for (Tensor _x : Drop.head(coeffs, 1))
      x = x.reciprocal().add(_x);
    return x;
  }
}
