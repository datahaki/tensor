// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.sca.Chop;

public enum MatrixRankSvd {
  ;
  /** @param matrix with numeric precision entries
   * @return rank of matrix */
  public static int of(Tensor matrix) {
    return of(SingularValueDecomposition.of(Unprotect.dimension1Hint(matrix) <= matrix.length() //
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
