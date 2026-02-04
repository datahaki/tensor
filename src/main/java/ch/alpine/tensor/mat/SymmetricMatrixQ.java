// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ConstraintSquareMatrixQ;
import ch.alpine.tensor.sca.Chop;

/** consistent with Mathematica:
 * SymmetricMatrixQ[ {} ] == false
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SymmetricMatrixQ.html">SymmetricMatrixQ</a>
 * 
 * @see HermitianMatrixQ
 * @see UnitaryMatrixQ
 * @see AntisymmetricMatrixQ */
public class SymmetricMatrixQ extends ConstraintSquareMatrixQ {
  public static final ConstraintSquareMatrixQ INSTANCE = new SymmetricMatrixQ(Tolerance.CHOP);

  public SymmetricMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor defect(Tensor v) {
    return Transpose.of(v).subtract(v);
  }
}
