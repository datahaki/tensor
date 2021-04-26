// code by jph
package ch.ethz.idsc.tensor.mat.gr;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class InfluenceMatrixSplit extends InfluenceMatrixBase implements Serializable {
  private final Tensor design;
  private final Tensor d_pinv;
  private Tensor matrix = null;

  /** @param design
   * @param d_pinv */
  public InfluenceMatrixSplit(Tensor design, Tensor d_pinv) {
    this.design = design;
    this.d_pinv = d_pinv;
  }

  @Override // from InfluenceMatrix
  public Tensor matrix() {
    return Objects.isNull(matrix) //
        ? matrix = design.dot(d_pinv)
        : matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    return vector.dot(design).dot(d_pinv);
  }
}
