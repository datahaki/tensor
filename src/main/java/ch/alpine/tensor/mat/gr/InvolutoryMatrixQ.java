// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ZeroDefectSquareMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** determines whether matrix P is involutory, i.e. whether
 * P is its own inverse, i.e. P . P == Id */
public class InvolutoryMatrixQ extends ZeroDefectSquareMatrixQ {
  public static final ZeroDefectSquareMatrixQ INSTANCE = new InvolutoryMatrixQ(Tolerance.CHOP);

  public InvolutoryMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor defect(Tensor matrix) {
    return IdentityMatrix.inplaceSub(matrix.dot(matrix));
  }
}
