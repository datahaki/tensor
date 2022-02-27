// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.pd.Orthogonalize;

/** EXPERIMENTAL
 * 
 * @see Orthogonalize */
public class QRMathematica extends QRDecompositionBase implements Serializable {
  /** @param qrDecomposition of matrix with dimensions n x m
   * @return */
  public static QRDecomposition wrap(QRDecomposition qrDecomposition) {
    return new QRMathematica(qrDecomposition);
  }

  // ---
  private final QRDecomposition qrDecomposition;
  private final int length;

  private QRMathematica(QRDecomposition qrDecomposition) {
    this.qrDecomposition = qrDecomposition;
    Tensor R = qrDecomposition.getR();
    length = Math.min(R.length(), Unprotect.dimension1(R));
  }

  @Override // from QRDecomposition
  public Tensor getQConjugateTranspose() {
    return Tensor.of(qrDecomposition.getQConjugateTranspose().stream().limit(length)); // min(n, m) x n
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return Tensor.of(qrDecomposition.getR().stream().limit(length)); // min(n, m) x m
  }

  @Override
  public int[] sigma() {
    return qrDecomposition.sigma();
  }
}
