// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;

/** EXPERIMENTAL
 * 
 * @see Orthogonalize */
public class QRMathematica implements QRDecomposition, Serializable {
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
  public Tensor getInverseQ() {
    return Tensor.of(qrDecomposition.getInverseQ().stream().limit(length)); // min(n, m) x n
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return Tensor.of(qrDecomposition.getR().stream().limit(length)); // min(n, m) x m
  }

  @Override // from QRDecomposition
  public Tensor getQ() {
    return ConjugateTranspose.of(getInverseQ()); // n x min(n, m)
  }

  @Override // from QRDecomposition
  public Scalar det() {
    return qrDecomposition.det();
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", //
        QRDecomposition.class.getSimpleName(), //
        Tensors.message(getQ(), getR()));
  }
}
