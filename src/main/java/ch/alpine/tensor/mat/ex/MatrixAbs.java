// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.sca.Abs;

/** Reference:
 * "Linear Algebra Learning from Data", p.290
 * by Gilbert Strang, 2019 */
public enum MatrixAbs {
  ;
  public static Tensor ofSymmetric(Tensor matrix) {
    return StaticHelper.mapEv(Eigensystem.ofSymmetric(matrix, Tolerance.CHOP), Abs.FUNCTION);
  }

  public static Tensor ofHermitian(Tensor matrix) {
    return StaticHelper.mapEv(Eigensystem.ofHermitian(matrix, Tolerance.CHOP), Abs.FUNCTION);
  }
}
