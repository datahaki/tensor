// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.ev.Eigensystem;

/* package */ enum Eigenvalues {
  ;
  /** @param matrix symmetric
   * @param scalarUnaryOperator applied to eigenvalues
   * @return resulting matrix is basis of given matrix
   * @throws Exception if input is not a real symmetric matrix */
  public static Tensor ofSymmetric_map(Tensor matrix, ScalarUnaryOperator scalarUnaryOperator) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor values = eigensystem.values().map(scalarUnaryOperator);
    return Transpose.of(eigensystem.vectors()).dot(values.pmul(eigensystem.vectors()));
  }
}
