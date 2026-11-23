// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;

/** {@link #of(Tensor)} requies the input to have array structure of level 2
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Diagonal.html">Diagonal</a> */
public enum Diagonal {
  ;
  /** @param matrix
   * @return vector of entries on diagonal of given matrix
   * @throws Exception if tensor is a scalar
   * @throws Exception if tensors on level 1 have different {@link Tensor#length()} */
  public static Tensor of(Tensor matrix) {
    return of(matrix, 0);
  }

  /** @param matrix
   * @param k
   * @return diagonal off by k from center diagonal
   * @throws if k indexes an off-diagonal outside of matrix */
  public static Tensor of(Tensor matrix, int k) {
    int rows = matrix.length();
    int cols = Unprotect.dimension1(matrix);
    return 0 <= k //
        ? Tensors.vector(i -> matrix.get(i, i + k), Math.min(rows, cols - k))
        : Tensors.vector(i -> matrix.get(i - k, i), Math.min(rows + k, cols));
  }
}
