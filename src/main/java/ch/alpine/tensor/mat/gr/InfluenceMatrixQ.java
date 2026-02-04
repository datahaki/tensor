// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.chq.ConstraintSquareMatrixQ;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** checks if matrix is hermitian and idempotent
 * whether given matrix is an influence matrix, i.e. whether given matrix equals
 * to ConjugateTranspose of matrix and satisfies the {@link IdempotentMatrixQ} predicate
 * 
 * the matrix {{1,1},{0,0}} is idempotent but not hermitian
 * 
 * @see HermitianMatrixQ
 * @see IdempotentMatrixQ */
public class InfluenceMatrixQ extends ConstraintSquareMatrixQ {
  public static final ConstraintSquareMatrixQ INSTANCE = new InfluenceMatrixQ(Tolerance.CHOP);

  public InfluenceMatrixQ(Chop chop) {
    super(chop);
  }

  @Override
  public Tensor defect(Tensor p) {
    return Join.of( //
        HermitianMatrixQ.INSTANCE.defect(p), // P == ConjugateTranspose[P]
        IdempotentMatrixQ.INSTANCE.defect(p)); // P . P == P
  }
}
