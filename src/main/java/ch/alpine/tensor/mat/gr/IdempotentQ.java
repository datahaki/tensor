// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** determines whether matrix P is idempotent, i.e. P . P == P */
public enum IdempotentQ {
  ;
  /** @param matrix
   * @param chop
   * @return */
  public static boolean of(Tensor matrix, Chop chop) {
    return SquareMatrixQ.of(matrix) //
        && chop.isClose(matrix, matrix.dot(matrix));
  }

  /** @param matrix
   * @return */
  public static boolean of(Tensor matrix) {
    return of(matrix, Tolerance.CHOP);
  }
}
