// code by jph
package ch.alpine.tensor.mat;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.ex.MatrixSqrt;

/* package */ class PolarDecompositionSqrt implements PolarDecomposition, Serializable {
  private final Tensor matrix;
  private final MatrixSqrt matrixSqrt;

  public PolarDecompositionSqrt(Tensor matrix) {
    this.matrix = matrix;
    matrixSqrt = MatrixSqrt.of(MatrixDotConjugateTranspose.of(matrix));
  }

  @Override // from PolarDecomposition
  public Tensor getS() {
    return matrixSqrt.sqrt();
  }

  @Override // from PolarDecomposition
  public Tensor getQ() {
    return matrixSqrt.sqrt_inverse().dot(matrix);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", PolarDecomposition.class.getSimpleName(), Tensors.message(getS(), getQ()));
  }
}
