// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ enum StaticHelper {
  ;
  /** @param matrix
   * @param scalarUnaryOperator applied to eigenvalues
   * @return */
  public static Tensor ofSymmetric(Tensor matrix, ScalarUnaryOperator scalarUnaryOperator) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor values = eigensystem.values().map(scalarUnaryOperator);
    return Transpose.of(eigensystem.vectors()).dot(values.pmul(eigensystem.vectors()));
  }
}
