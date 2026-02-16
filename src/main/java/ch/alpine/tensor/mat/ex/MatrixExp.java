// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;

/** Reference:
 * "The Scaling and Squaring Method for the Matrix Exponential Revisited"
 * by Nick Higham, 2004
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixExp.html">MatrixExp</a> */
public enum MatrixExp {
  ;
  public static final ThreadLocal<Integer> MAX_ITERATIONS = ThreadLocal.withInitial(() -> 128);
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  /** @param matrix square
   * @return exponential of given matrix exp(m) = I + m + m^2/2 + m^3/6 + ...
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    long exponent = 1;
    try {
      exponent = exponent(Matrix2Norm.bound(matrix));
    } catch (Exception exception) {
      // ---
    }
    return MatrixPower.of(MatrixExpSeries.FUNCTION.apply(matrix.multiply(Rational.of(1, exponent))), exponent);
  }

  /** @param norm
   * @return power of 2 */
  @PackageTestAccess
  static long exponent(Scalar norm) {
    return 1 << Ceiling.longValueExact(LOG2.apply(norm.add(norm.one())));
  }

  /** Hint: use {@link Symmetrize} on result for extra precision
   * 
   * @param matrix
   * @return symmetric matrix */
  public static Tensor ofSymmetric(Tensor matrix) {
    return Eigensystem.ofSymmetric(matrix).map(Exp.FUNCTION);
  }

  /** @param matrix hermitian
   * @return hermitian matrix */
  public static Tensor ofHermitian(Tensor matrix) {
    return Eigensystem.ofHermitian(matrix).map(Exp.FUNCTION);
  }
}
