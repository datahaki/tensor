// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** Hint: implementation makes use of eigenvalue decomposition of
 * real-valued symmetric matrices for various applications.
 * 
 * @see MatrixExp
 * @see MatrixLog
 * @see MatrixPower */
/* package */ enum StaticHelper {
  ;
  public static final Function<Integer, Tensor> IDENTITY_MATRIX = Cache.of(IdentityMatrix::of, 16);

  /** @param matrix
   * @param scalarUnaryOperator applied to eigenvalues
   * @return */
  public static Tensor ofSymmetric(Tensor matrix, ScalarUnaryOperator scalarUnaryOperator) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor values = eigensystem.values().map(scalarUnaryOperator);
    return Transpose.of(eigensystem.vectors()).dot(values.pmul(eigensystem.vectors()));
  }
}
