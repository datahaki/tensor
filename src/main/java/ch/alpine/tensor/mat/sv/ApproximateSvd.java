// code by jph
package ch.alpine.tensor.mat.sv;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.qr.QRDecomposition;

/** Reference:
 * "Linear Algebra Learning from Data", p.144
 * by G. Strang, 2019 */
public class ApproximateSvd implements SingularValueDecomposition, Serializable {
  /** @param qrDecomposition
   * @return */
  public static SingularValueDecomposition of(QRDecomposition qrDecomposition) {
    return new ApproximateSvd(qrDecomposition);
  }

  // ---
  private final QRDecomposition qrDecomposition;
  private final SingularValueDecomposition svd;

  private ApproximateSvd(QRDecomposition qrDecomposition) {
    this.qrDecomposition = qrDecomposition;
    // TODO not general sigma
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
