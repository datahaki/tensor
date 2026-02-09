// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.TensorComparator;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.InvertUnlessZero;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SingularValueList.html">SingularValueList</a> */
public enum SingularValueList {
  ;
  /** @param matrix
   * @return vector of singular values of given matrix ordered from large to small.
   * All singular values are non-negative. */
  public static Tensor of(Tensor matrix) {
    return of(SingularValueDecomposition.of(matrix.length() < Unprotect.dimension1Hint(matrix) //
        ? Transpose.of(matrix)
        : matrix));
  }

  /** @param svd
   * @return vector of singular values of given matrix ordered from large to small.
   * All singular values are non-negative. */
  public static Tensor of(SingularValueDecomposition svd) {
    return Tensor.of(svd.values().stream() //
        .sorted(TensorComparator.INSTANCE.reversed()));
  }

  /** Careful: the order of the inverted singular values correspond to the order of
   * singular values provided by {@link SingularValueDecomposition#values()}
   * 
   * @param svd
   * @param chop
   * @return inverted singular values unless zero */
  public static Tensor inverted(SingularValueDecomposition svd, Chop chop) {
    return svd.values().maps(chop).maps(InvertUnlessZero.FUNCTION);
  }
}
