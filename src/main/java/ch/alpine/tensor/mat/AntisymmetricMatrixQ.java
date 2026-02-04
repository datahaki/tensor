// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ZeroDefectSquareMatrixQ;
import ch.alpine.tensor.sca.Chop;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/AntisymmetricMatrixQ.html">AntisymmetricMatrixQ</a>
 * 
 * @see HermitianMatrixQ */
public class AntisymmetricMatrixQ extends ZeroDefectSquareMatrixQ {
  public static final ZeroDefectSquareMatrixQ INSTANCE = new AntisymmetricMatrixQ(Tolerance.CHOP);

  public AntisymmetricMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor defect(Tensor v) {
    return Transpose.of(v).add(v);
  }
}
