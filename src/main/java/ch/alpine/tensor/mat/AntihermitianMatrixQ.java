// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ZeroDefectSquareMatrixQ;
import ch.alpine.tensor.sca.Chop;

/** A is anti-hermitian if A = -ConjugateTranspose[A]
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/AntihermitianMatrixQ.html">AntihermitianMatrixQ</a>
 * 
 * https://en.wikipedia.org/wiki/Skew-Hermitian_matrix */
public class AntihermitianMatrixQ extends ZeroDefectSquareMatrixQ {
  public static final ZeroDefectSquareMatrixQ INSTANCE = new AntihermitianMatrixQ(Tolerance.CHOP);

  public AntihermitianMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor defect(Tensor v) {
    return ConjugateTranspose.of(v).add(v);
  }
}
