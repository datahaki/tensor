// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;

/** 1-norm */
public enum Matrix1Norm {
  ;
  /** @param matrix
   * @return 1-norm of given matrix */
  public static Scalar of(Tensor matrix) {
    return (Scalar) Total.of(matrix.maps(Abs.FUNCTION)).stream().reduce(Max::of).orElseThrow();
  }
}
