// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ConstraintSquareMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** determines whether matrix P is involutory, i.e. whether
 * P is its own inverse, i.e. P . P == Id */
public class InvolutoryMatrixQ extends ConstraintSquareMatrixQ {
  public static final ConstraintSquareMatrixQ INSTANCE = new InvolutoryMatrixQ(Tolerance.CHOP);

  public InvolutoryMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor constraint(Tensor matrix) {
    return IdentityMatrix.inplaceSub(matrix.dot(matrix));
  }
}
