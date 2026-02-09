// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.sca.Conjugate;

/** the purpose of the implementation to make obsolete the explicit
 * computation of the {@link Transpose} */
public enum MatrixDotConjugateTranspose {
  ;
  /** @param matrix
   * @param tensor
   * @return */
  public static Tensor of(Tensor matrix, Tensor tensor) {
    return MatrixDotTranspose.of(matrix, tensor.maps(Conjugate.FUNCTION));
  }

  /** @param matrix
   * @return matrix . ConjugateTranspose(matrix) */
  public static Tensor self(Tensor matrix) {
    return of(matrix, matrix);
  }
}
