// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Quote:
 * The index of an n x n nilpotent matrix is always less than or equal to n.
 * The determinant and trace of a nilpotent matrix are always zero.
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Nilpotent_matrix */
public enum NilpotentMatrixQ {
  ;
  /** @param matrix square
   * @param chop
   * @return whether matrix ^ k == 0 for some k
   * @throws Exception if given matrix is not square */
  public static boolean of(Tensor matrix, Chop chop) {
    if (chop.isZero(Trace.of(matrix))) {
      int n = matrix.length();
      if (!ExactTensorQ.of(matrix))
        matrix = matrix.multiply(Sqrt.FUNCTION.apply(RealScalar.of(n)).divide(Matrix2Norm.bound(matrix)));
      return chop.allZero(MatrixPower.of(matrix, n));
    }
    return false;
  }

  /** @param matrix square
   * @return whether matrix ^ k == 0 for some k
   * @throws Exception if given matrix is not square */
  public static boolean of(Tensor matrix) {
    return of(matrix, Tolerance.CHOP);
  }
}
