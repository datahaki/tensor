// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.re.Pivot;
import ch.alpine.tensor.mat.re.Pivots;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Inverse.html">Inverse</a>
 * 
 * @see PseudoInverse */
public enum Inverse {
  ;
  /** @param matrix with square dimensions
   * @return inverse of given matrix
   * @throws Exception if given matrix is not invertible */
  public static Tensor of(Tensor matrix) {
    return of(matrix, Pivots.selection(matrix));
  }

  /** function doesn't invoke Scalar::abs but pivots at the first non-zero column entry
   * 
   * @param matrix with square dimensions
   * @param pivot
   * @return inverse of given matrix
   * @throws Exception if given matrix is not invertible */
  public static Tensor of(Tensor matrix, Pivot pivot) {
    return LinearSolve.of(matrix, IdentityMatrix.of(matrix), pivot);
  }
}
