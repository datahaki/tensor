// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.sca.Log;

/* package */ enum MatrixLog1 {
  ;
  /** @param matrix of size 1 x 1
   * @return */
  public static Tensor of(Tensor matrix) {
    MatrixQ.requireSize(matrix, 1, 1);
    return matrix.map(Log.FUNCTION);
  }
}
