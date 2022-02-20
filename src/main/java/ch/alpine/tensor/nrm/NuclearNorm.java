// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.red.Total;

/** Reference:
 * Gilbert Strang */
public enum NuclearNorm {
  ;
  /** @param matrix
   * @return sum of singular values */
  public static Scalar of(Tensor matrix) {
    return Total.ofVector(SingularValueList.of(matrix));
  }
}
