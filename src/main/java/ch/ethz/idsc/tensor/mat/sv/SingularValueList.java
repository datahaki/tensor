// code by jph
package ch.ethz.idsc.tensor.mat.sv;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.InvertUnlessZero;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SingularValueList.html">SingularValueList</a> */
public enum SingularValueList {
  ;
  /** @param svd
   * @param chop
   * @return inverted singular values unless zero */
  public static Tensor inverted(SingularValueDecomposition svd, Chop chop) {
    return svd.values().map(chop).map(InvertUnlessZero.FUNCTION);
  }
}
