// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.MatrixDotConjugateTranspose;
import ch.alpine.tensor.mat.ex.MatrixSqrt;

/* package */ class SqrtPu extends PolarDecompositionSqrt {
  public SqrtPu(Tensor matrix) {
    super(matrix, MatrixSqrt.of(MatrixDotConjugateTranspose.of(matrix)));
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return matrixSqrt.sqrt_inverse().dot(matrix);
  }
}
