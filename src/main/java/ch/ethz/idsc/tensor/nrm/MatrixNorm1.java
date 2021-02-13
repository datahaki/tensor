// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Abs;

/** 1-norm */
public enum MatrixNorm1 {
  ;
  /** @param matrix
   * @return 1-norm of given matrix */
  public static Scalar of(Tensor matrix) {
    return (Scalar) Total.of(Abs.of(matrix)).stream().reduce(Max::of).get();
  }
}
