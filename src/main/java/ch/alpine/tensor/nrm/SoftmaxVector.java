// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.exp.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SoftmaxLayer.html">SoftmaxLayer</a> */
public enum SoftmaxVector {
  ;
  /** @param vector
   * @return
   * @throws Exception if vector is empty */
  public static Tensor of(Tensor vector) {
    Scalar n_max = vector.stream().reduce(Max::of).map(Scalar.class::cast).orElseThrow().negate();
    return NormalizeTotal.FUNCTION.apply(Tensor.of(vector.stream().map(n_max::add).map(Exp.FUNCTION)));
  }
}
