// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.red.Trace;

/** https://en.wikipedia.org/wiki/Killing_form */
public enum KillingForm {
  ;
  /** @param ad tensor of Lie-algebra
   * @return Killing-form of Lie-algebra
   * @throws Exception if rank of ad is not 3 */
  public static Tensor of(Tensor ad) {
    return SquareMatrixQ.INSTANCE.requireMember(Trace.of(ad.dot(ad), 0, 3));
  }
}
