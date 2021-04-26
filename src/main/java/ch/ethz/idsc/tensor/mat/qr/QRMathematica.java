// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.mat.Orthogonalize;

/** EXPERIMENTAL
 * 
 * @see Orthogonalize */
public class QRMathematica extends QRDecompositionBase implements Serializable {
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
    Tensor R = qrDecomposition.getR();
    length = Math.min(R.length(), Unprotect.dimension1(R));
  }

  @Override // from QRDecomposition
  public Tensor getQTranspose() {
    return Tensor.of(qrDecomposition.getQTranspose().stream().limit(length)); // min(n, m) x n
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return Tensor.of(qrDecomposition.getR().stream().limit(length)); // min(n, m) x m
  }

  @Override // from QRDecomposition
  public Scalar det() {
    return qrDecomposition.det();
  }
}
