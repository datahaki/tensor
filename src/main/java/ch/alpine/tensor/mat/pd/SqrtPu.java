// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.mat.ex.MatrixSqrt;
import ch.alpine.tensor.sca.Im;

/* package */ class SqrtPu extends PolarDecompositionSqrt {
  /** @param matrix
   * @return */
  public static PolarDecompositionSqrt of(Tensor matrix) {
    Tensor square = MatrixDotConjugateTranspose.of(matrix);
    boolean isReal = Flatten.scalars(square) //
        .map(Im.FUNCTION) //
        .allMatch(Scalars::isZero);
    MatrixSqrt matrixSqrt = isReal //
        ? MatrixSqrt.ofSymmetric(square)
        : MatrixSqrt.ofHermitian(square);
    return new SqrtPu(matrix, matrixSqrt);
  }

  private SqrtPu(Tensor matrix, MatrixSqrt matrixSqrt) {
    super(matrix, matrixSqrt);
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return matrixSqrt.sqrt_inverse().dot(matrix);
  }
}
