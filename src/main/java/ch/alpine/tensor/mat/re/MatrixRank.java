// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.qr.GramSchmidt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixRank.html">MatrixRank</a> */
public enum MatrixRank {
  ;
  /** If all entries of the matrix are of exact precision,
   * the matrix rank is computed from the result of {@link RowReduce}.
   * Otherwise, {@link GramSchmidt} is used.
   * 
   * @param matrix with exact and/or numeric precision entries
   * @return rank of matrix
   * @see ExactTensorQ */
  public static int of(Tensor matrix) {
    return ExactTensorQ.of(matrix) //
        ? usingRowReduce(matrix)
        : Dimensions.of(GramSchmidt.of(matrix).getQConjugateTranspose()).stream() //
            .reduce(Math::min) //
            .orElseThrow();
  }

  /** @param matrix with exact precision entries
   * @return rank of matrix */
  private static int usingRowReduce(Tensor matrix) {
    int n = matrix.length();
    int m = Unprotect.dimension1Hint(matrix);
    Tensor lhs = RowReduce.of(matrix, Pivots.FIRST_NON_ZERO);
    int j = 0;
    int c0 = 0;
    while (j < n && c0 < m)
      if (Scalars.nonZero(lhs.Get(j, c0++))) // <- careful: c0 is modified
        ++j;
    return j;
  }
}
