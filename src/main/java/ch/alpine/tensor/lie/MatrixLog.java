// code by jph
package ch.alpine.tensor.lie;

import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Sign;

/** Hint: implementation uses inverse of the scaling and squaring procedure that
 * involves repeated matrix square roots.
 * 
 * References:
 * "Matrix Computations" 4th Edition
 * by Gene H. Golub, Charles F. Van Loan, 2012
 * 
 * "Approximating the Logarithm of a Matrix to Specified Accuracy"
 * by Sheung Hun Cheng, Nicholas J. Higham, Charles S. Kenny, Alan J. Laub 2001
 *
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixLog.html">MatrixLog</a>
 * 
 * @see MatrixExp */
public enum MatrixLog {
  ;
  private static final int MAX_EXPONENT = 20;
  private static final Scalar RHO_MAX = RealScalar.of(0.6);
  private static final int MAX_ITERATIONS = 96;

  /** Hint: currently only matrices of dimensions 2 x 2 are supported
   * as well as symmetric positive definite matrices
   * 
   * @param matrix
   * @return
   * @throws Exception if computation is not supported for given matrix */
  public static Tensor of(Tensor matrix) {
    switch (matrix.length()) {
    case 1:
      return MatrixLog1.of(matrix);
    case 2:
      return MatrixLog2.of(matrix);
    default:
      return _of(matrix);
    }
  }

  /* package */ static Tensor _of(Tensor matrix) {
    Tensor id = StaticHelper.IDENTITY_MATRIX.apply(matrix.length());
    Tensor rem = matrix.subtract(id);
    List<DenmanBeaversDet> deque = new LinkedList<>();
    for (int count = 0; count < MAX_EXPONENT; ++count) {
      Scalar rho_max = Matrix2Norm.bound(rem);
      if (Scalars.lessThan(rho_max, RHO_MAX)) {
        Tensor sum = matrix.map(Scalar::zero);
        Scalar factor = RealScalar.ONE;
        for (DenmanBeaversDet denmanBeaversDet : deque) {
          sum = sum.add(denmanBeaversDet.mk().subtract(id).multiply(factor));
          factor = factor.add(factor);
        }
        return sum.add(series1p(rem).multiply(factor));
      }
      DenmanBeaversDet denmanBeaversDet = new DenmanBeaversDet(matrix, Tolerance.CHOP);
      deque.add(denmanBeaversDet);
      matrix = denmanBeaversDet.sqrt();
      rem = matrix.subtract(id);
    }
    throw TensorRuntimeException.of(matrix);
  }

  /** Hint: use {@link Symmetrize} on result for extra precision
   * 
   * @param matrix symmetric with all positive eigenvalues
   * @return
   * @see PositiveDefiniteMatrixQ */
  public static Tensor ofSymmetric(Tensor matrix) {
    return Eigenvalues.ofSymmetric_map(matrix, MatrixLog::logPositive);
  }

  // helper function
  private static Scalar logPositive(Scalar scalar) {
    return Log.FUNCTION.apply(Sign.requirePositive(scalar));
  }

  /** @param x square matrix with spectral radius below 1
   * @return log[ I + x ]
   * @throws Exception if given matrix is non-square
   * @see Math#log1p(double) */
  /* package */ static Tensor series1p(Tensor x) {
    Tensor nxt = x;
    Tensor sum = nxt;
    for (int k = 2; k < MAX_ITERATIONS; ++k) {
      nxt = nxt.dot(x);
      Scalar den = DoubleScalar.of(Integers.isEven(k) ? -k : k);
      if (sum.equals(sum = sum.add(nxt.divide(den))))
        return sum;
    }
    throw TensorRuntimeException.of(x); // insufficient convergence
  }
}
