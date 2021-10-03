// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Conjugate;

public enum MatrixDotConjugateTranspose {
  ;
  /** @param matrix
   * @return matrix . ConjugateTranspose(matrix) */
  public static Tensor of(Tensor matrix) {
    return MatrixDotTranspose.of(matrix, Conjugate.of(matrix));
  }
}
