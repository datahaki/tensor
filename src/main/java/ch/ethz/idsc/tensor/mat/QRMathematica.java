// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

/** @see Orthogonalize */
/* package */ class QRMathematica implements QRDecomposition, Serializable {
  private static final long serialVersionUID = 1117960065957628905L;

  /** @param qrDecomposition of matrix with dimensions n x m
   * @return */
  public static QRDecomposition wrap(QRDecomposition qrDecomposition) {
    return new QRMathematica(qrDecomposition);
  }

  /***************************************************/
  private final QRDecomposition qrDecomposition;
  private final int length;

  private QRMathematica(QRDecomposition qrDecomposition) {
    this.qrDecomposition = qrDecomposition;
    Tensor r = qrDecomposition.getR();
    length = Math.min(r.length(), Unprotect.dimension1(r));
  }

  @Override // from QRDecomposition
  public Tensor getInverseQ() {
    return Tensor.of(qrDecomposition.getInverseQ().stream().limit(length)); // min(n, m) x n
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return Tensor.of(qrDecomposition.getR().stream().limit(length)); // n x min(n, m)
  }

  @Override // from QRDecomposition
  public Tensor getQ() {
    return Tensor.of(qrDecomposition.getQ().stream().map(row -> row.extract(0, length))); // min(n, m) x m
  }

  @Override // from QRDecomposition
  public Scalar det() {
    return qrDecomposition.det();
  }
}
