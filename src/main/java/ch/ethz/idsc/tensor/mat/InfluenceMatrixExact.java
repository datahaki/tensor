// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class InfluenceMatrixExact extends InfluenceMatrixBase implements Serializable {
  private static final long serialVersionUID = 174847716560122416L;
  // ---
  private final Tensor design;
  private final Tensor matrix;

  public InfluenceMatrixExact(Tensor design, Tensor matrix) {
    this.design = design;
    this.matrix = matrix;
  }

  @Override
  public Tensor matrix() {
    return matrix;
  }

  @Override
  public synchronized Tensor image(Tensor vector) {
    return matrix.dot(vector);
  }
}
