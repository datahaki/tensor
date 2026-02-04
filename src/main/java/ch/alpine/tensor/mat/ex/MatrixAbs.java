// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.sca.Abs;

/** Reference:
 * "Linear Algebra Learning from Data", p.290
 * by Gilbert Strang, 2019 */
public enum MatrixAbs {
  ;
  /** @param matrix
   * @return matrix with eigenvalues as absolute values of eigenvalues of given matrix
   * @see Abs */
  public static Tensor ofSymmetric(Tensor matrix) {
    return Eigensystem.ofSymmetric(matrix, Tolerance.CHOP).map(Abs.FUNCTION);
  }

  /** @param matrix
   * @return matrix with eigenvalues as absolute values of eigenvalues of given matrix
   * @see Abs */
  public static Tensor ofHermitian(Tensor matrix) {
    return Eigensystem.ofHermitian(matrix, Tolerance.CHOP).map(Abs.FUNCTION);
  }
}
