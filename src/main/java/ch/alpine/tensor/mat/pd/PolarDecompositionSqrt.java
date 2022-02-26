// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ex.MatrixSqrt;

/* package */ abstract class PolarDecompositionSqrt extends PolarDecompositionBase implements Serializable {
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
}
