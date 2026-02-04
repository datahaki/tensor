// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.GroupInterface;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.BinaryPower;

/** for the group of invertible, square matrices */
/* package */ enum MatrixGroups implements GroupInterface<Tensor> {
  GENERAL_LINEAR {
    @Override // from GroupInterface
    public Tensor invert(Tensor matrix) {
      return Inverse.of(matrix);
    }
  },
  // ORTHOGONAL {
  // @Override // from GroupInterface
  // public Tensor invert(Tensor matrix) {
  // return Transpose.of(matrix);
  // }
  // },
  // UNITARY {
  // @Override // from GroupInterface
  // public Tensor invert(Tensor matrix) {
  // return ConjugateTranspose.of(matrix);
  // }
  // }
  ;

  private final BinaryPower<Tensor> binaryPower = new BinaryPower<>(this);

  public BinaryPower<Tensor> binaryPower() {
    return binaryPower;
  }

  @Override // from GroupInterface
  public final Tensor neutral(Tensor matrix) {
    return IdentityMatrix.of(matrix);
  }

  @Override // from GroupInterface
  public final Tensor combine(Tensor matrix1, Tensor matrix2) {
    return matrix1.dot(matrix2);
  }

  @Override // from Object
  public final String toString() {
    return "MatrixGroups[" + name() + "]";
  }
}
