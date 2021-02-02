// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Frobenius;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** References:
 * "Matrix Computations", 4th Edition
 * Section 2.3.2 Some Matrix Norm Properties
 * 
 * Wikipedia:
 * https://en.wikipedia.org/wiki/Matrix_norm
 * 
 * @see MatrixExp
 * @see MatrixLog */
/* package */ enum Norm2Bound {
  ;
  /** @param matrix
   * @return upper bound to 2-norm of given matrix up to numerical precision */
  public static Scalar ofMatrix(Tensor matrix) {
    Scalar n1 = Norm._1.ofMatrix(matrix);
    Scalar ni = Norm.INFINITY.ofMatrix(matrix);
    return Min.of( //
        Sqrt.FUNCTION.apply(n1.multiply(ni)), // Hoelder's inequality
        Frobenius.of(matrix));
  }
}
