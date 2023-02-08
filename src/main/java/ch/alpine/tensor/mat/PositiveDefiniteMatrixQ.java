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
  /** Reference:
   * "Matrix Computations" Theorem 4.2.3
   * 
   * @param tensor
   * @return */
  public static boolean of(Tensor tensor) {
    return ofHermitian(ConjugateTranspose.of(tensor).add(tensor)); // divide by 2
  }

  /** @param tensor
   * @return true if tensor is a positive definite matrix
   * @throws Throw if result cannot be established */
  public static boolean ofHermitian(Tensor tensor) {
    return StaticHelper.definite(tensor, Chop.NONE, Sign::isPositive);
  }
}
