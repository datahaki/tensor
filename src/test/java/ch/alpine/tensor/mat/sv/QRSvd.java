// code by jph
package ch.alpine.tensor.mat.sv;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.qr.GramSchmidt;
import ch.alpine.tensor.mat.qr.QRDecomposition;

/** Experiments show that QRSvd is faster than {@link SingularValueDecomposition}
 * for thin matrices, i.e. when a matrix has many more rows than columns.
 * 
 * <p>Reference:
 * "Linear Algebra Learning from Data", p.144
 * by G. Strang, 2019 */
class QRSvd implements SingularValueDecomposition, Serializable {
  /** @param matrix with maximal rank
   * @return */
  public static SingularValueDecomposition of(Tensor matrix) {
    return of(GramSchmidt.of(matrix));
  }

  /** @param qrDecomposition with r as square matrix
   * @return
   * @see GramSchmidt
   * @see SquareMatrixQ */
  public static SingularValueDecomposition of(QRDecomposition qrDecomposition) {
    return new QRSvd(qrDecomposition);
  }

  // ---
  private final QRDecomposition qrDecomposition;
  private final SingularValueDecomposition svd;

  private QRSvd(QRDecomposition qrDecomposition) {
    this.qrDecomposition = qrDecomposition;
    svd = SingularValueDecomposition.of(qrDecomposition.getR());
  }

  @Override // from SingularValueDecomposition
  public Tensor getU() {
    return qrDecomposition.getQ().dot(svd.getU());
  }

  @Override // from SingularValueDecomposition
  public Tensor values() {
    return svd.values();
  }

  @Override // from SingularValueDecomposition
  public Tensor getV() {
    return svd.getV();
  }
}
