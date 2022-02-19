// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.ex.MatrixSqrt;

/* package */ abstract class PolarDecompositionSqrt implements PolarDecomposition, Serializable {
  final Tensor matrix;
  final MatrixSqrt matrixSqrt;

  public PolarDecompositionSqrt(Tensor matrix, MatrixSqrt matrixSqrt) {
    this.matrix = matrix;
    this.matrixSqrt = matrixSqrt;
  }

  @Override // from PolarDecomposition
  public final Tensor getPositiveSemidefinite() {
    return matrixSqrt.sqrt();
  }

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", PolarDecomposition.class.getSimpleName(), Tensors.message(getPositiveSemidefinite(), getUnitary()));
  }
}
