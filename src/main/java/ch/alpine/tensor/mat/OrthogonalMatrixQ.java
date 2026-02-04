// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ConstraintMemberQ;
import ch.alpine.tensor.sca.Chop;

/** Mathematica's definition:
 * "A matrix m is orthogonal if m.Transpose[m] is the identity matrix."
 * 
 * If the matrix has more rows than columns, Mathematica checks whether
 * Transpose[m].m == IdentityMatrix.
 * The tensor library does not check that but returns false in that case.
 * 
 * <p>The determinant of an orthogonal matrix is either +1 or -1.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/OrthogonalMatrixQ.html">OrthogonalMatrixQ</a>
 * 
 * @see UnitaryMatrixQ */
public class OrthogonalMatrixQ extends ConstraintMemberQ {
  public static final ConstraintMemberQ INSTANCE = new OrthogonalMatrixQ(Tolerance.CHOP);

  public OrthogonalMatrixQ(Chop chop) {
    super(2, chop);
  }

  @Override
  public Tensor constraint(Tensor p) {
    return IdentityMatrix.inplaceSub(MatrixDotTranspose.self(p));
  }
}
