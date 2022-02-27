// code by jph
package ch.alpine.tensor.mat.ex;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.red.Times;
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
/* package */ class MatrixSqrtEigensystem implements MatrixSqrt, Serializable {
  private final Tensor avec;
  private final Tensor ainv;
  private final Tensor sqrt;

  /** @param eigensystem */
  public MatrixSqrtEigensystem(Eigensystem eigensystem) {
    avec = eigensystem.vectors();
    ainv = ConjugateTranspose.of(avec);
    sqrt = eigensystem.values().map(Sqrt.FUNCTION);
  }

  @Override // from MatrixSqrt
  public Tensor sqrt() {
    return ainv.dot(Times.of(sqrt, avec));
  }

  @Override // from MatrixSqrt
  public Tensor sqrt_inverse() {
    return ainv.dot(Times.of(sqrt.map(Scalar::reciprocal), avec));
  }
}
