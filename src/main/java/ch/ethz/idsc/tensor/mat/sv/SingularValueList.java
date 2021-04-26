// code by jph
package ch.ethz.idsc.tensor.mat.sv;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.TensorComparator;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.InvertUnlessZero;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SingularValueList.html">SingularValueList</a> */
public enum SingularValueList {
  ;
  /** @param matrix
   * @return vector of singular values of given matrix ordered from large to small.
   * All singular values are non-negative. */
  public static Tensor of(Tensor matrix) {
    return Tensor.of(SingularValueDecomposition.of(matrix.length() < Unprotect.dimension1(matrix) //
        ? Transpose.of(matrix)
        : matrix).values().stream() //
        .sorted(TensorComparator.INSTANCE.reversed()));
  }

  /** Careful: the order of the inverted singular values correspond to the order of
   * singular values provided by {@link SingularValueDecomposition#values()}
   * 
   * @param svd
   * @param chop
   * @return inverted singular values unless zero */
  public static Tensor inverted(SingularValueDecomposition svd, Chop chop) {
    return svd.values().map(chop).map(InvertUnlessZero.FUNCTION);
  }
}
