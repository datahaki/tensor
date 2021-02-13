// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.nrm.MatrixNorm2;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.N;

/** Reference:
 * "The Scaling and Squaring Method for the Matrix Exponential Revisited"
 * by Nick Higham, 2004
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixExp.html">MatrixExp</a> */
public enum MatrixExp {
  ;
  private static final ScalarUnaryOperator LOG2 = Log.base(2);
  /** with scaling the series typically converges in few steps */
  private static final int MAX_ITERATIONS = 128;

  /** @param matrix square
   * @return exponential of given matrix exp(m) = I + m + m^2/2 + m^3/6 + ...
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    long exponent = exponent(MatrixNorm2.bound(matrix));
    return MatrixPower.of(series(matrix.multiply(RationalScalar.of(1, exponent))), exponent);
  }

  /** @param norm
   * @return power of 2 */
  /* package */ static long exponent(Scalar norm) {
    return 1 << Ceiling.longValueExact(LOG2.apply(norm.add(RealScalar.ONE)));
  }

  /** @param matrix square
   * @return
   * @throws Exception if given matrix is non-square */
  /* package */ static Tensor series(Tensor matrix) {
    int n = matrix.length();
    Tensor nxt = matrix;
    Tensor sum = StaticHelper.IDENTITY_MATRIX.apply(n).add(nxt);
    for (int k = 2; k <= n; ++k) {
      nxt = nxt.dot(matrix).divide(RealScalar.of(k));
      sum = sum.add(nxt);
      if (Chop.NONE.allZero(nxt))
        return sum;
    }
    sum = N.DOUBLE.of(sum); // switch to numeric precision
    for (int k = n + 1; k < MAX_ITERATIONS; ++k) {
      nxt = nxt.dot(matrix).divide(RealScalar.of(k));
      if (sum.equals(sum = sum.add(nxt)))
        return sum;
    }
    throw TensorRuntimeException.of(matrix); // insufficient convergence
  }

  /** Hint: use {@link Symmetrize} on result for extra precision
   * 
   * @param matrix
   * @return symmetric matrix */
  public static Tensor ofSymmetric(Tensor matrix) {
    return Eigenvalues.ofSymmetric_map(matrix, Exp.FUNCTION);
  }
}
