// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.sca.Log;

/* package */ enum MatrixLog1 {
  ;
  /** @param matrix of size 1 x 1
   * @return */
  public static Tensor of(Tensor matrix) {
    MatrixQ.requireSize(matrix, 1, 1);
    return matrix.map(Log.FUNCTION);
  }
}
