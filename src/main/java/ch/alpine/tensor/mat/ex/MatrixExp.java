// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.sca.Exp;

/** Reference:
 * "The Scaling and Squaring Method for the Matrix Exponential Revisited"
 * by Nick Higham, 2004
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixExp.html">MatrixExp</a> */
public enum MatrixExp {
  ;
  /** @param matrix square
   * @return exponential of given matrix exp(m) = I + m + m^2/2 + m^3/6 + ...
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    long exponent = StaticHelper.exponent(Matrix2Norm.bound(matrix));
    return MatrixPower.of(MatrixExpSeries.FUNCTION.apply(matrix.multiply(RationalScalar.of(1, exponent))), exponent);
  }

  /** Hint: use {@link Symmetrize} on result for extra precision
   * 
   * @param matrix
   * @return symmetric matrix */
  public static Tensor ofSymmetric(Tensor matrix) {
    return StaticHelper.mapEv(Eigensystem.ofSymmetric(matrix, Tolerance.CHOP), Exp.FUNCTION);
  }

  /** @param matrix hermitian
   * @return hermitian matrix */
  public static Tensor ofHermitian(Tensor matrix) {
    return StaticHelper.mapEv(Eigensystem.ofHermitian(matrix, Tolerance.CHOP), Exp.FUNCTION);
  }
}
