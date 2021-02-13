// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.nrm.Norm;
import ch.ethz.idsc.tensor.nrm.Normalize;
import ch.ethz.idsc.tensor.sca.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SoftmaxLayer.html">SoftmaxLayer</a> */
public enum SoftmaxLayer {
  ;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._1);

  /** @param vector
   * @return
   * @throws Exception if vector is empty */
  public static Tensor of(Tensor vector) {
    return NORMALIZE.apply(Exp.of(vector));
  }
}
