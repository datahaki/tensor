// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Sign;

public interface QRSignOperator {
  /** @param xk
   * @return
   * @see Sign */
  Scalar sign(Scalar xk);

  /** @return if {@link QRDecomposition#det()} is exact, and false if det is only valid up to sign */
  boolean isDetExact();
}
