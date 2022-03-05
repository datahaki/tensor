// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.mat.sv.SingularValueList;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.pow.Sqrt;

public enum Matrix2Norm {
  ;
  /** @param matrix
   * @return 2-norm of given matrix
   * @see SingularValueList */
  public static Scalar of(Tensor matrix) {
    return SingularValueList.of(matrix).Get(0);
  }

  /** References:
   * "Matrix Computations", 4th Edition
   * Section 2.3.2 Some Matrix Norm Properties
   * 
   * Wikipedia:
   * https://en.wikipedia.org/wiki/Matrix_norm
   * 
   * Used in {@link MatrixExp}, {@link MatrixLog}, and the Ben Israel Cohen
   * iteration
   * 
   * @param matrix
   * @return upper bound to 2-norm of given matrix up to numerical precision */
  public static Scalar bound(Tensor matrix) {
    Scalar n1 = Matrix1Norm.of(matrix);
    Scalar ni = MatrixInfinityNorm.of(matrix);
    return Min.of( //
        Sqrt.FUNCTION.apply(n1.multiply(ni)), // Hoelder's inequality
        FrobeniusNorm.of(matrix));
  }
}
