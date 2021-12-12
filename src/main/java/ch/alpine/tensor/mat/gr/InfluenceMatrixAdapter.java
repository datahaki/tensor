// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;

/* package */ class InfluenceMatrixAdapter extends InfluenceMatrixBase implements Serializable {
  private final Tensor matrix;

  /** @param influence matrix == design . design^+ */
  public InfluenceMatrixAdapter(Tensor matrix) {
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