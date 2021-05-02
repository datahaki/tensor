// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.re.Pivot;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.mat.re.RowReduce;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.sca.Chop;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixRank.html">MatrixRank</a> */
public enum MatrixRank {
  ;
  /** if the matrix contains only exact precision entries,
   * the method {@link #usingRowReduce(Tensor)} is used,
   * otherwise {@link #usingSvd(Tensor)} is used.
   * 
   * @param matrix with exact and/or numeric precision entries
   * @return rank of matrix */
  public static int of(Tensor matrix) {
    return ExactTensorQ.of(matrix) //
        ? usingRowReduce(matrix, Pivots.FIRST_NON_ZERO)
        : usingSvd(matrix);
  }

  public static int usingRowReduce(Tensor matrix) {
    return usingRowReduce(matrix, Pivots.ARGMAX_ABS);
  }

  /** @param matrix with exact precision entries
   * @param pivot
   * @return rank of matrix */
  public static int usingRowReduce(Tensor matrix, Pivot pivot) {
    int n = matrix.length();
    int m = Unprotect.dimension1(matrix);
    Tensor lhs = RowReduce.of(matrix, pivot);
    int j = 0;
    int c0 = 0;
    while (j < n && c0 < m)
      if (Scalars.nonZero(lhs.Get(j, c0++))) // <- careful: c0 is modified
        ++j;
    return j;
  }

  /** @param matrix with numeric precision entries
   * @return rank of matrix */
  public static int usingSvd(Tensor matrix) {
    return of(SingularValueDecomposition.of(Unprotect.dimension1(matrix) <= matrix.length() //
        ? matrix
        : Transpose.of(matrix)));
  }

  /** @param svd
   * @param chop threshold
   * @return rank of matrix decomposed in svd */
  public static int of(SingularValueDecomposition svd, Chop chop) {
    return Math.toIntExact(svd.values().stream() //
        .map(Scalar.class::cast) //
        .map(chop) //
        .filter(Scalars::nonZero) //
        .count());
  }

  /** @param svd
   * @return rank of matrix decomposed in svd */
  public static int of(SingularValueDecomposition svd) {
    return of(svd, Tolerance.CHOP);
  }
}
