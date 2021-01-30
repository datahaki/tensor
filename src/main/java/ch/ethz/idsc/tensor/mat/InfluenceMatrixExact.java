// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class InfluenceMatrixExact extends InfluenceMatrixBase {
  private static final long serialVersionUID = -592326580204883709L;
  // ---
  private final Tensor matrix;

  /** @param matrix == design . design^+ */
  public InfluenceMatrixExact(Tensor matrix, Scalar one) {
    super(one);
    this.matrix = matrix;
  }

  @Override // from InfluenceMatrixBase
  public Tensor matrix() {
    return matrix;
  }

  @Override // from InfluenceMatrixBase
  public Tensor image(Tensor vector) {
    // LONGTERM is vector . matrix is better / more consistent !?
    return matrix.dot(vector);
  }

  @Override // from InfluenceMatrixBase
  protected int length() {
    return matrix.length();
  }
}
