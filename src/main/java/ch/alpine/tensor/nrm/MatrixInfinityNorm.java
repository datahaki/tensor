// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.red.Max;

public enum MatrixInfinityNorm {
  ;
  /** @param matrix
   * @return infinity-norm of given matrix */
  public static Scalar of(Tensor matrix) {
    MatrixQ.require(matrix);
    return matrix.stream() //
        .map(Vector1Norm::of) //
        .reduce(Max::of).get();
  }
}
