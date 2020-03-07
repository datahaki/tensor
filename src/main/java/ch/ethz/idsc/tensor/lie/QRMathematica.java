// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

/** @see Orthogonalize */
/* package */ class QRMathematica implements QRDecomposition, Serializable {
  /** @param qrDecomposition
   * @return */
  public static QRDecomposition wrap(QRDecomposition qrDecomposition) {
    return new QRMathematica(qrDecomposition);
  }

  /***************************************************/
  private final QRDecomposition qrDecomposition;
  private final int length;

  public QRMathematica(QRDecomposition qrDecomposition) {
    this.qrDecomposition = qrDecomposition;
    Tensor r = qrDecomposition.getR();
    length = Math.min(r.length(), Unprotect.dimension1(r));
  }

  @Override
  public Tensor getInverseQ() {
    return qrDecomposition.getInverseQ().extract(0, length);
  }

  @Override
  public Tensor getR() {
    return qrDecomposition.getR().extract(0, length);
  }

  @Override
  public Tensor getQ() {
    return Tensor.of(qrDecomposition.getQ().stream().map(row -> row.extract(0, length)));
  }

  @Override
  public Scalar det() {
    return qrDecomposition.det();
  }
}
