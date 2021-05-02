// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Sign;

public interface QRSignOperator {
  /** @param xk
   * @return
   * @see Sign */
  Scalar sign(Scalar xk);

  /** @return if {@link QRDecomposition#det()} is exact, and false if det is only valid up to sign */
  boolean isDetExact();
}
