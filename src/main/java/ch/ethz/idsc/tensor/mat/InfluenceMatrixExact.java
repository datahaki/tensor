// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class InfluenceMatrixExact extends InfluenceMatrixBase implements Serializable {
  private static final long serialVersionUID = -5637844310417226371L;
  // ---
  private final Tensor matrix;

  /** @param matrix == design . design^+ */
  public InfluenceMatrixExact(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor matrix() {
    return matrix;
  }

  @Override // from InfluenceMatrix
  public Tensor image(Tensor vector) {
    // LONGTERM is vector . matrix is better / more consistent !?
    return matrix.dot(vector);
  }

  @Override // from InfluenceMatrixBase
  protected int length() {
    return matrix.length();
  }
}
