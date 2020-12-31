// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.PositiveDefiniteMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sign;

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
  private static final Scalar RHO_MAX = RealScalar.of(0.99);
  private static final int MAX_ITERATIONS = 100;

  /** Hint: currently only matrices of dimensions 2 x 2 are supported
   * as well as symmetric positive definite matrices
   * 
   * @param matrix
   * @return
   * @throws Exception if computation is not supported for given matrix */
  public static Tensor of(Tensor matrix) {
    int dim1 = Unprotect.dimension1(matrix);
    if (matrix.length() == 2)
      if (dim1 == 2)
        return MatrixLog2.of(matrix);
    // ---
    int n = matrix.length();
    Tensor id = IdentityMatrix.of(n);
    int roots = 0;
    for (; roots < MAX_EXPONENT; ++roots) {
      Tensor rem = matrix.subtract(id);
      Scalar rho = Max.of(Norm._1.ofMatrix(rem), Norm.INFINITY.ofMatrix(rem)); // no less than 2-norm
      if (Scalars.lessThan(rho, RHO_MAX))
        break;
      matrix = new MatrixSqrtImpl(matrix, Tolerance.CHOP).sqrt();
    }
    return series(matrix).multiply(Power.of(2, roots));
  }

  /** Hint: use {@link Symmetrize} on result for extra precision
   * 
   * @param matrix symmetric with all positive eigenvalues
   * @return
   * @see PositiveDefiniteMatrixQ */
  public static Tensor ofSymmetric(Tensor matrix) {
    return StaticHelper.ofSymmetric(matrix, MatrixLog::logPositive);
  }

  // helper function
  private static Scalar logPositive(Scalar scalar) {
    return Log.FUNCTION.apply(Sign.requirePositive(scalar));
  }

  /** @param matrix square with spectral radius below 1
   * @return
   * @throws Exception if given matrix is non-square */
  /* package */ static Tensor series(Tensor matrix) {
    Tensor x = matrix.subtract(IdentityMatrix.of(matrix.length()));
    Tensor nxt = x;
    Tensor sum = nxt;
    for (int k = 2; k < MAX_ITERATIONS; ++k) {
      nxt = nxt.dot(x);
      Tensor prv = sum;
      sum = sum.add(nxt.divide(RealScalar.of(k % 2 == 0 ? -k : k)));
      if (Chop.NONE.isClose(sum, prv))
        return sum;
    }
    throw TensorRuntimeException.of(matrix); // insufficient convergence
  }
}
