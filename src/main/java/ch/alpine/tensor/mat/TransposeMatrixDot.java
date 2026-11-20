// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;

/** useful to keep track of the operation
 * Transpose.of(matrix).dot(tensor)
 * in the code base
 * 
 * however, implementation does not give a speedup yet! */
public enum TransposeMatrixDot {
  ;
  /** @param matrix
   * @param tensor
   * @return Transpose.of(matrix).dot(tensor) */
  public static Tensor of(Tensor matrix, Tensor tensor) {
    return Transpose.of(matrix).dot(tensor);
    // matrix.Get(0, 0);
    // // slower
    // int m = Unprotect.dimension1Hint(matrix);
    // return Tensor.of(IntStream.range(0, m) //
    // .mapToObj(i -> matrix.get(Tensor.ALL, i).dot(tensor)));
  }

  public static Tensor self(Tensor matrix) {
    return of(matrix, matrix);
  }
}
