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
  /** @param logits vector
   * @return vector of probabilities that sum up to 1
   * @throws Exception if vector is empty */
  public static Tensor of(Tensor logits) {
    Scalar n_max = logits.stream().reduce(Max::of).map(Scalar.class::cast).orElseThrow().negate();
    return NormalizeTotal.FUNCTION.apply(Tensor.of(logits.stream().map(n_max::add).map(Exp.FUNCTION)));
  }
}
