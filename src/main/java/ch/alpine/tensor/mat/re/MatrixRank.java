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
  /** If the matrix contains only exact precision entries,
   * the method {@link #usingRowReduce(Tensor)} is used.
   * Otherwise, {@link #usingGramSchmidt(Tensor)} is used.
   * 
   * @param matrix with exact and/or numeric precision entries
   * @return rank of matrix */
  public static int of(Tensor matrix) {
    return ExactTensorQ.of(matrix) //
        ? usingRowReduce(matrix, Pivots.FIRST_NON_ZERO)
        : usingGramSchmidt(matrix);
  }

  /** @param matrix with exact precision entries
   * @return rank of matrix */
  public static int usingRowReduce(Tensor matrix) {
    return usingRowReduce(matrix, Pivots.ARGMAX_ABS);
  }

  /** @param matrix with exact precision entries
   * @param pivot
   * @return rank of matrix */
  public static int usingRowReduce(Tensor matrix, Pivot pivot) {
    int n = matrix.length();
    int m = Unprotect.dimension1Hint(matrix);
    Tensor lhs = RowReduce.of(matrix, pivot);
    int j = 0;
    int c0 = 0;
    while (j < n && c0 < m)
      if (Scalars.nonZero(lhs.Get(j, c0++))) // <- careful: c0 is modified
        ++j;
    return j;
  }

  /** @param matrix
   * @return matrix rank of matrix */
  public static int usingGramSchmidt(Tensor matrix) {
    return Dimensions.of(GramSchmidt.of(matrix).getQConjugateTranspose()).stream() //
        .reduce(Math::min).orElse(0);
  }
}
