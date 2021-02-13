// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class InfluenceMatrixExact extends InfluenceMatrixBase implements Serializable {
  private static final long serialVersionUID = -5637844310417226371L;
  // ---
  private final Tensor matrix; // design . design^+

  /** @param design matrix
   * @throws Exception if design matrix does not have maximal rank */
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

  @Override // from InfluenceMatrixBase
  protected int length() {
    return matrix.length();
  }
}
