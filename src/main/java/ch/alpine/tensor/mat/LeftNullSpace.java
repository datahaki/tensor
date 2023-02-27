// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.re.Pivot;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.mat.re.RowReduce;

/** Let N = LeftNullSpace[A]. If N is non-empty, then N.A == 0.
 * 
 * <pre>
 * LeftNullSpace[matrix] == NullSpace[Transpose[matrix]]
 * </pre>
 * 
 * <p>The tensor library provides LeftNullSpace for convenience.
 * The command "LeftNullSpace" does not exist in Mathematica.
 * 
 * @see NullSpace */
/* package */ enum LeftNullSpace {
  ;
  /** @param matrix with exact precision
   * @return */
  public static Tensor usingRowReduce(Tensor matrix) {
    ExactTensorQ.require(matrix);
    return usingRowReduce(matrix, Pivots.FIRST_NON_ZERO);
  }

  /** @param matrix
   * @param pivot
   * @return list of vectors that span the left nullspace of given matrix */
  private static Tensor usingRowReduce(Tensor matrix, Pivot pivot) {
    int rows = matrix.length();
    Tensor lhs = RowReduce.of(Join.of(1, matrix, DiagonalMatrix.of(rows, matrix.Get(0, 0).one())), pivot);
    int cols = Unprotect.dimension1Hint(matrix);
    int j = 0;
    int c0 = 0;
    while (c0 < cols && j < rows)
      if (Scalars.nonZero(lhs.Get(j, c0++))) // <- careful: c0 is modified
        ++j;
    return Tensor.of(lhs.extract(j, rows).stream().map(row -> row.extract(cols, cols + rows)));
  }
}
