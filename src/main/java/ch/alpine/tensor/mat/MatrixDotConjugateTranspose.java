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
   * @return matrix . ConjugateTranspose(matrix) */
  public static Tensor of(Tensor matrix) {
    return MatrixDotTranspose.of(matrix, Conjugate.of(matrix));
  }
}
