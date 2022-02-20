// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.red.Times;

/** Reference:
 * "Linear Algebra Learning from Data"
 * by G. Strang, 2019 */
/* package */ class PolarDecompositionSvd extends PolarDecompositionBase {
  /** @param matrix
   * @return */
  public static PolarDecomposition up(Tensor matrix) {
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    return new PolarDecompositionSvd(svd, svd.getV());
  }

  /** @param matrix
   * @return */
  public static PolarDecomposition pu(Tensor matrix) {
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    return new PolarDecompositionSvd(svd, svd.getU());
  }

  // ---
  private final SingularValueDecomposition svd;
  private final Tensor orth;

  public PolarDecompositionSvd(SingularValueDecomposition svd, Tensor orth) {
    this.svd = svd;
    this.orth = orth;
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return MatrixDotTranspose.of(svd.getU(), svd.getV());
  }

  @Override // from PolarDecomposition
  public Tensor getPositiveSemidefinite() {
    return MatrixDotTranspose.of(Tensor.of(orth.stream().map(Times.operator(svd.values()))), orth);
  }
}
