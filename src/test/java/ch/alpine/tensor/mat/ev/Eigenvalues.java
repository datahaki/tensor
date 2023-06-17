// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Tensor;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Eigenvalues.html">Eigenvalues</a> */
enum Eigenvalues {
  ;
  // TODO TENSOR IMPL also for real matrix -> complex eigenvalues
  /** @param matrix symmetric
   * @return vector of eigenvalues */
  public static Tensor ofSymmetric(Tensor matrix) {
    return Eigensystem.ofSymmetric(matrix).values();
  }

  /** @param matrix hermitian
   * @return vector of eigenvalues */
  public static Tensor ofHermitian(Tensor matrix) {
    return Eigensystem.ofHermitian(matrix).values();
  }
}
