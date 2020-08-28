// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixLog.html">MatrixLog</a> */
public enum MatrixLog {
  ;
  private static final int MAX_ITERATIONS = 500;

  /** Hint: currently only matrices of dimensions 2 x 2 are supported
   * 
   * @param matrix
   * @return
   * @throws Exception if computation is not supported for given matrix */
  public static Tensor of(Tensor matrix) {
    int dim1 = Unprotect.dimension1(matrix);
    if (matrix.length() == 2) {
      if (dim1 == 2)
        return MatrixLog2.of(matrix);
    }
    throw new UnsupportedOperationException();
  }

  /** @param matrix symmetric
   * @return */
  public static Tensor ofSymmetric(Tensor matrix) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Scalar exponent = RationalScalar.of(1, 2); // TODO adapt exponent to matrix
    Tensor avec = eigensystem.vectors();
    Tensor m = Transpose.of(avec).dot(eigensystem.values().map(Power.function(exponent)).pmul(avec));
    return series(m).multiply(exponent.reciprocal());
  }

  /** @param matrix square
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
