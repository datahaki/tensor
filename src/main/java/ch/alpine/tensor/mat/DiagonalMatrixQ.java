// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.Chop;

/** consistent with Mathematica, in particular DiagonalMatrix[{}] results in error.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DiagonalMatrixQ.html">DiagonalMatrixQ</a> */
public enum DiagonalMatrixQ {
  ;
  /** @param matrix
   * @param chop
   * @return */
  public static boolean of(Tensor matrix, Chop chop) {
    if (SquareMatrixQ.INSTANCE.isMember(matrix)) {
      int n = matrix.length();
      for (int i = 0; i < n; ++i)
        for (int j = 0; j < n; ++j)
          if (i != j && !chop.isZero(matrix.Get(i, j)))
            return false;
      return true;
    }
    return false;
  }

  /** @param matrix
   * @return */
  public static boolean of(Tensor matrix) {
    return of(matrix, Tolerance.CHOP);
  }

  /** @param tensor
   * @param chop
   * @return */
  public static Tensor require(Tensor tensor, Chop chop) {
    if (of(tensor, chop))
      return tensor;
    throw new Throw(tensor);
  }

  /** @param tensor
   * @return given tensor
   * @throws Exception if given tensor is not a square matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
