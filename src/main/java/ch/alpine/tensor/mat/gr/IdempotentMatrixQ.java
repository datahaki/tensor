// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ConstraintSquareMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** determines whether matrix P is idempotent, i.e. P . P == P
 * 
 * Example: the following matrix is idempotent
 * <pre>
 * {
 * {0, 1},
 * {0, 1}
 * }
 * </pre> */
public class IdempotentMatrixQ extends ConstraintSquareMatrixQ {
  public static final ConstraintSquareMatrixQ INSTANCE = new IdempotentMatrixQ(Tolerance.CHOP);

  public IdempotentMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor defect(Tensor matrix) {
    return matrix.dot(matrix).subtract(matrix);
  }
}
