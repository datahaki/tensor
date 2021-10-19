// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.spa.SparseArray;

/** dot product Dot[a, Transpose[b]] optimized for matrices (and restricted to them) */
public enum MatrixDotTranspose {
  ;
  /** the return value of the function is always identical to
   * <pre>
   * Dot[matrix, Transpose[tensor]]
   * </pre>
   * However, the expression Dot[a, Transpose[b]] works for a broader range of input,
   * for instance there, a may be a vector.
   * 
   * @param matrix
   * @param tensor of rank at least 2
   * @return matrix . Transpose[tensor]
   * @throws Exception if requirements on input parameters are violated */
  public static Tensor of(Tensor matrix, Tensor tensor) {
    matrix.Get(0, 0); // fail fast if input is not a matrix
    return tensor instanceof SparseArray //
        ? matrix.dot(Transpose.of(tensor))
        : Tensor.of(matrix.stream().map(row -> Tensor.of(tensor.stream().map(row::dot))));
  }
}
