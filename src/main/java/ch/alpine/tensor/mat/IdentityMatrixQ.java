// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ZeroDefectSquareMatrixQ;
import ch.alpine.tensor.sca.Chop;

/** determines whether matrix is close to the IdentityMatrix */
public class IdentityMatrixQ extends ZeroDefectSquareMatrixQ {
  public static final ZeroDefectSquareMatrixQ INSTANCE = new IdentityMatrixQ(Tolerance.CHOP);

  public IdentityMatrixQ(Chop chop) {
    super(chop);
  }

  @Override // from ZeroDefectArrayQ
  public Tensor defect(Tensor matrix) {
    return IdentityMatrix.inplaceSub(matrix.copy());
  }
}
