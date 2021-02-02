// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.num.GroupInterface;

/** for the group of invertible, square matrices */
/* package */ enum MatrixProduct implements GroupInterface<Tensor> {
  INSTANCE;

  @Override // from GroupInterface
  public Tensor neutral(Tensor matrix) {
    return IdentityMatrix.of(matrix);
  }

  @Override // from GroupInterface
  public Tensor invert(Tensor matrix) {
    return Inverse.of(matrix);
  }

  @Override // from GroupInterface
  public Tensor combine(Tensor matrix1, Tensor matrix2) {
    return matrix1.dot(matrix2);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s", getClass().getSimpleName());
  }
}
