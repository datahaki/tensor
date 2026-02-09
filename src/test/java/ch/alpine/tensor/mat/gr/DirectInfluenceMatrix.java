// code by jph
package ch.alpine.tensor.mat.gr;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.pow.Sqrt;

class DirectInfluenceMatrix implements InfluenceMatrix, Serializable {
  public static InfluenceMatrix of(Tensor matrix) {
    return new DirectInfluenceMatrix(matrix);
  }

  private final Tensor matrix;

  public DirectInfluenceMatrix(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override
  public Tensor matrix() {
    return matrix;
  }

  @Override
  public Tensor leverages() {
    return Diagonal.of(matrix());
  }

  @Override
  public Tensor leverages_sqrt() {
    return leverages().maps(Sqrt.FUNCTION);
  }

  @Override
  public Tensor image(Tensor vector) {
    return vector.dot(matrix);
  }

  @Override // from InfluenceMatrix
  public Tensor kernel(Tensor vector) {
    return vector.subtract(image(vector));
  }
}
