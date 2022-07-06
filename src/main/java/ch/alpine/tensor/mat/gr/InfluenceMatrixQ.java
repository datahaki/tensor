// code by jph
package ch.alpine.tensor.mat.gr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** checks if matrix is symmetric and idempotent
 * 
 * @see SymmetricMatrixQ */
public enum InfluenceMatrixQ {
  ;
  /** @param matrix
   * @param chop
   * @return whether given matrix equals to ConjugateTranspose of matrix
   * and satisfies the {@link IdempotentQ} predicate */
  public static boolean of(Tensor matrix, Chop chop) {
    return HermitianMatrixQ.of(matrix, chop) // P = ConjugateTranspose[P]
        && IdempotentQ.of(matrix, chop); // P . P == P
  }

  /** @param matrix
   * @return */
  public static boolean of(Tensor matrix) {
    return of(matrix, Tolerance.CHOP);
  }

  /** @param matrix
   * @param chop
   * @return */
  public static Tensor require(Tensor matrix, Chop chop) {
    if (of(matrix, chop))
      return matrix;
    throw Throw.of(matrix);
  }

  /** @param matrix
   * @return */
  public static Tensor require(Tensor matrix) {
    return require(matrix, Tolerance.CHOP);
  }
}
