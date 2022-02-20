// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.ex.MatrixSqrt;

/* package */ class SqrtUp extends PolarDecompositionSqrt {
  public SqrtUp(Tensor matrix) {
    super(matrix, MatrixSqrt.of(ConjugateTranspose.of(matrix).dot(matrix)));
  }

  @Override // from PolarDecomposition
  public Tensor getUnitary() {
    return matrix.dot(matrixSqrt.sqrt_inverse());
  }
}
