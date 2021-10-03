// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.GroupInterface;

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
    return getClass().getSimpleName();
  }
}
