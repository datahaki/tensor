// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.red.Max;

public enum MatrixNormInfinity {
  ;
  /** @param matrix
   * @return infinity-norm of given matrix */
  public static Scalar of(Tensor matrix) {
    MatrixQ.require(matrix);
    return matrix.stream() //
        .map(VectorNorm1::of) //
        .reduce(Max::of).get();
  }
}
