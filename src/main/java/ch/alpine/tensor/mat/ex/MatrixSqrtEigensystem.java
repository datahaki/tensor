// code by jph
package ch.alpine.tensor.mat.ex;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.sca.pow.Sqrt;

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
/* package */ record MatrixSqrtEigensystem(Eigensystem eigensystem) implements MatrixSqrt, Serializable {
  @Override // from MatrixSqrt
  public Tensor sqrt() {
    return eigensystem.map(Sqrt.FUNCTION);
  }

  @Override // from MatrixSqrt
  public Tensor sqrt_inverse() {
    return eigensystem.map(s -> Sqrt.FUNCTION.apply(s).reciprocal());
  }
}
