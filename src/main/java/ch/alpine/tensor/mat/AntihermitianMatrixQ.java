// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ConstraintSquareMatrixQ;
import ch.alpine.tensor.sca.Chop;

/** A is anti-hermitian if A = -ConjugateTranspose[A]
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/AntihermitianMatrixQ.html">AntihermitianMatrixQ</a>
 * 
 * https://en.wikipedia.org/wiki/Skew-Hermitian_matrix */
public class AntihermitianMatrixQ extends ConstraintSquareMatrixQ {
  public static final ConstraintSquareMatrixQ INSTANCE = new AntihermitianMatrixQ(Tolerance.CHOP);

  public AntihermitianMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor constraint(Tensor v) {
    return ConjugateTranspose.of(v).add(v);
  }
}
