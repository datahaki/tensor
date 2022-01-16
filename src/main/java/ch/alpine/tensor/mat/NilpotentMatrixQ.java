// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ex.MatrixPower;

public enum NilpotentMatrixQ {
  ;
  /** @param matrix square
   * @return whether matrix ^ k == 0 for some k */
  public static boolean of(Tensor matrix) {
    return Tolerance.CHOP.allZero(MatrixPower.of(matrix, matrix.length()));
  }
}
