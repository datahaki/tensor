// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/NegativeDefiniteMatrixQ.html">NegativeDefiniteMatrixQ</a>
 * 
 * @see PositiveDefiniteMatrixQ */
public enum NegativeDefiniteMatrixQ {
  ;
  /** @param tensor
   * @return true if tensor is a negative definite matrix
   * @throws Throw if result cannot be established */
  public static boolean ofHermitian(Tensor tensor) {
    return StaticHelper.definite(tensor, Chop.NONE, Sign::isNegative);
  }
}
