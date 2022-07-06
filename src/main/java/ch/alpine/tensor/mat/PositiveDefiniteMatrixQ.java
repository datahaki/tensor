// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PositiveDefiniteMatrixQ.html">PositiveDefiniteMatrixQ</a>
 * 
 * @see NegativeDefiniteMatrixQ */
public enum PositiveDefiniteMatrixQ {
  ;
  /** @param tensor
   * @param chop
   * @return true if tensor is a positive definite matrix
   * @throws Throw if result cannot be established */
  public static boolean ofHermitian(Tensor tensor, Chop chop) {
    return StaticHelper.definite(tensor, chop, Sign::isPositive);
  }

  /** @param tensor
   * @return true if tensor is a positive definite matrix
   * @throws Throw if result cannot be established */
  public static boolean ofHermitian(Tensor tensor) {
    return ofHermitian(tensor, Tolerance.CHOP);
  }
}
