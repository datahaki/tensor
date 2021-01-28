// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

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
    return of(matrix, RealScalar.ONE, Pivots.ARGMAX_ABS);
  }

  /** function doesn't invoke Scalar::abs but pivots at the first non-zero column entry
   * 
   * @param matrix with square dimensions
   * @param one diagonal element of identity, for instance {@link RealScalar#ONE}
   * @param pivot
   * @return inverse of given matrix
   * @throws Exception if given matrix is not invertible */
  public static Tensor of(Tensor matrix, Scalar one, Pivot pivot) {
    return LinearSolve.of(matrix, DiagonalMatrix.of(matrix.length(), one), pivot);
  }
}
