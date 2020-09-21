// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Sign;

public interface QRSignOperator {
  /** @param xk
   * @return
   * @see Sign */
  Scalar sign(Scalar xk);
}
