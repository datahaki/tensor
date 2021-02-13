// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.VectorNorm1;
import ch.ethz.idsc.tensor.sca.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SoftmaxLayer.html">SoftmaxLayer</a> */
public enum SoftmaxLayer {
  ;
  /** @param vector
   * @return
   * @throws Exception if vector is empty */
  public static Tensor of(Tensor vector) {
    return VectorNorm1.NORMALIZE.apply(Exp.of(vector));
  }
}
