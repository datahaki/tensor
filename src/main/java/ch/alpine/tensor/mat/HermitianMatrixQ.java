// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ConstraintSquareMatrixQ;
import ch.alpine.tensor.sca.Chop;

/** Quote from Wikipedia: A Hermitian matrix (or self-adjoint matrix) is
 * a complex square matrix that is equal to its own conjugate transpose.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HermitianMatrixQ.html">HermitianMatrixQ</a>
 * 
 * @see SymmetricMatrixQ
 * @see AntisymmetricMatrixQ */
public class HermitianMatrixQ extends ConstraintSquareMatrixQ {
  public static final ConstraintSquareMatrixQ INSTANCE = new HermitianMatrixQ(Tolerance.CHOP);

  public HermitianMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor defect(Tensor v) {
    return ConjugateTranspose.of(v).subtract(v);
  }
}
