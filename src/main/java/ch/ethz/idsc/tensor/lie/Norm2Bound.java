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
 * @see MatrixLog */
/* package */ enum Norm2Bound {
  ;
  /** @param matrix square
   * @return upper bound to 2-norm of given matrix */
  public static Scalar ofMatrix(Tensor matrix) {
    Scalar nf = Frobenius.NORM.ofMatrix(matrix);
    Scalar n1 = Norm._1.ofMatrix(matrix);
    Scalar ni = Norm.INFINITY.ofMatrix(matrix);
    Scalar nh = Sqrt.FUNCTION.apply(n1.multiply(ni)); // Hoelder's inequality
    return Min.of(nf, nh);
  }
}
