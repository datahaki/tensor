// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.ex.MatrixSqrt;
import ch.alpine.tensor.sca.Im;

/* package */ class SqrtUp extends PolarDecompositionSqrt {
  /** @param matrix
   * @return */
  public static PolarDecompositionSqrt of(Tensor matrix) {
    Tensor square = ConjugateTranspose.of(matrix).dot(matrix);
    return new SqrtUp(matrix, Im.allZero(square) //
        ? MatrixSqrt.ofSymmetric(square)
        : MatrixSqrt.ofHermitian(square));
  }

  private SqrtUp(Tensor matrix, MatrixSqrt matrixSqrt) {
    super(matrix, matrixSqrt);
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return matrix.dot(matrixSqrt.sqrt_inverse());
  }
}
