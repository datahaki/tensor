// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.ex.MatrixSqrt;
import ch.alpine.tensor.sca.Im;

/* package */ class SqrtUp extends PolarDecompositionSqrt {
  /** @param matrix
   * @return */
  public static PolarDecompositionSqrt of(Tensor matrix) {
    Tensor square = ConjugateTranspose.of(matrix).dot(matrix);
    boolean isReal = Flatten.scalars(square) //
        .map(Im.FUNCTION) //
        .allMatch(Scalars::isZero);
    MatrixSqrt matrixSqrt = isReal //
        ? MatrixSqrt.ofSymmetric(square)
        : MatrixSqrt.ofHermitian(square);
    return new SqrtUp(matrix, matrixSqrt);
  }

  private SqrtUp(Tensor matrix, MatrixSqrt matrixSqrt) {
    super(matrix, matrixSqrt);
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return matrix.dot(matrixSqrt.sqrt_inverse());
  }
}
