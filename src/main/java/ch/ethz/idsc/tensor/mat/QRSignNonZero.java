// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;

/** requires matrix to have maximal rank, as used in least square
 * 
 * @see LeastSquares
 * @see PseudoInverse */
/* package */ class QRSignNonZero implements QRSignOperator, Serializable {
  private static final long serialVersionUID = 2430310662230955277L;
  // ---
  private final QRSignOperator qrSignOperator;
  private final Chop chop;

  /** @param qrSignOperator
   * @param chop */
  public QRSignNonZero(QRSignOperator qrSignOperator, Chop chop) {
    this.qrSignOperator = Objects.requireNonNull(qrSignOperator);
    this.chop = Objects.requireNonNull(chop);
  }

  @Override // from QRSignOperator
  public Scalar sign(Scalar xk) {
    return qrSignOperator.sign(chop.requireNonZero(xk));
  }

  @Override // from QRSignOperator
  public boolean isDetExact() {
    return qrSignOperator.isDetExact();
  }
}
