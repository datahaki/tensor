// code by jph
package ch.alpine.tensor.lie;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.sca.Sqrt;

/** Determines the square root of a symmetric positive definite matrix.
 * 
 * "The symmetric square root of an SPD matrix is always defined and
 * moreover unique."
 * 
 * Reference:
 * "Riemannian Geometric Statistics in Medical Image Analysis", 2020
 * Edited by Xavier Pennec, Sommer, P. Thomas Fletcher, p. 80
 * 
 * @see MatrixPower */
/* package */ class MatrixSqrtSymmetric implements MatrixSqrt, Serializable {
  private final Tensor avec;
  private final Tensor ainv;
  private final Tensor sqrt;

  /** @param matrix
   * @throws Exception if input is not a real symmetric matrix */
  public MatrixSqrtSymmetric(Tensor matrix) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    avec = eigensystem.vectors();
    ainv = Transpose.of(avec);
    sqrt = eigensystem.values().map(Sqrt.FUNCTION);
  }

  @Override // from MatrixSqrt
  public Tensor sqrt() {
    return ainv.dot(sqrt.pmul(avec));
  }

  @Override // from MatrixSqrt
  public Tensor sqrt_inverse() {
    return ainv.dot(sqrt.map(Scalar::reciprocal).pmul(avec));
  }
}
