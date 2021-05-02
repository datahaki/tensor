// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;

/** consistent with Mathematica:
 * <pre>
 * UpperTriangularize[{{1}}, +1] == {{0}}
 * UpperTriangularize[{{1}}, +0] == {{1}}
 * UpperTriangularize[{{1}}, -1] == {{1}}
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UpperTriangularize.html">UpperTriangularize</a> */
public enum UpperTriangularize {
  ;
  /** retains the entries on the diagonal and above
   * 
   * @param matrix
   * @return
   * @throws Exception if given matrix is not an array of rank 2 */
  public static Tensor of(Tensor matrix) {
    return of(matrix, 0);
  }

  /** Example:
   * UpperTriangularize.of(matrix, 1)
   * retains the entries strictly above the diagonal
   * 
   * @param matrix
   * @param k
   * @return
   * @throws Exception if given matrix is not an array of rank 2 */
  public static Tensor of(Tensor matrix, int k) {
    return Tensors.matrix((i, j) -> k <= j - i ? matrix.Get(i, j) : matrix.Get(i, j).zero(), //
        matrix.length(), Unprotect.dimension1(matrix));
  }
}
