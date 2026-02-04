// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.mat.ex.MatrixSqrt;
import ch.alpine.tensor.sca.Im;

/* package */ class SqrtPu extends PolarDecompositionSqrt {
  /** @param matrix
   * @return */
  public static PolarDecompositionSqrt of(Tensor matrix) {
    Tensor square = MatrixDotConjugateTranspose.self(matrix);
    return new SqrtPu(matrix, Im.allZero(square) //
        ? MatrixSqrt.ofSymmetric(square)
        : MatrixSqrt.ofHermitian(square));
  }

  private SqrtPu(Tensor matrix, MatrixSqrt matrixSqrt) {
    super(matrix, matrixSqrt);
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return matrixSqrt.sqrt_inverse().dot(matrix);
  }
}
