// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.sca.exp.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SoftmaxLayer.html">SoftmaxLayer</a> */
public enum SoftmaxLayer {
  ;
  /** @param vector
   * @return
   * @throws Exception if vector is empty */
  public static Tensor of(Tensor vector) {
    return Vector1Norm.NORMALIZE.apply(vector.map(Exp.FUNCTION));
  }
}
