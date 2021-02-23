// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class InfluenceMatrixExact extends InfluenceMatrixBase implements Serializable {
  private static final long serialVersionUID = 8871352443406867104L;
  // ---
  private final Tensor matrix;

  /** @param influence matrix == design . design^+ */
  public InfluenceMatrixExact(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor matrix() {
    return matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    return vector.dot(matrix);
  }
}
