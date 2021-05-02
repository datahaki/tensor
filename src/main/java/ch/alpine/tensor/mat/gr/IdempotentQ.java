// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Chop;

public enum IdempotentQ {
  ;
  /** @param matrix
   * @param chop
   * @return */
  public static boolean of(Tensor matrix, Chop chop) {
    return chop.isClose(matrix, matrix.dot(matrix));
  }
}
