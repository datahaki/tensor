// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ConstraintMemberQ;
import ch.alpine.tensor.sca.Chop;

/** Mathematica's definition:
 * "A matrix m is unitary if m.ConjugateTranspose[m] is the identity matrix."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitaryMatrixQ.html">UnitaryMatrixQ</a> */
public class UnitaryMatrixQ extends ConstraintMemberQ {
  public static final ConstraintMemberQ INSTANCE = new UnitaryMatrixQ(Tolerance.CHOP);

  public UnitaryMatrixQ(Chop chop) {
    super(2, chop);
  }

  @Override
  public Tensor defect(Tensor p) {
    return IdentityMatrix.inplaceSub(MatrixDotConjugateTranspose.self(p));
  }
}
