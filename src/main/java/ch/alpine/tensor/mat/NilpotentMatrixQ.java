// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Chop;

/** Quote:
 * The index of an n x n nilpotent matrix is always less than or equal to n.
 * The determinant and trace of a nilpotent matrix are always zero.
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Nilpotent_matrix */
public enum NilpotentMatrixQ {
  ;
  /** @param matrix square
   * @param chop
   * @return whether matrix ^ k == 0 for some k
   * @throws Exception if given matrix is not square */
  public static boolean of(Tensor matrix, Chop chop) {
    // TODO TENSOR IMPL if inexact precision normalize using Matrix2Norm.bound(matrix)
    return chop.isZero(Trace.of(matrix)) //
        && chop.allZero(MatrixPower.of(matrix, matrix.length()));
  }

  /** @param matrix square
   * @return whether matrix ^ k == 0 for some k
   * @throws Exception if given matrix is not square */
  public static boolean of(Tensor matrix) {
    return of(matrix, Tolerance.CHOP);
  }
}
