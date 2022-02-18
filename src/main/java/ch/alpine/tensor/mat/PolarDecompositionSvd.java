// code by jph
package ch.alpine.tensor.mat;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

/* package */ class PolarDecompositionSvd implements PolarDecomposition, Serializable {
  private final SingularValueDecomposition svd;

  public PolarDecompositionSvd(SingularValueDecomposition svd) {
    this.svd = svd;
  }

  @Override // from PolarDecomposition
  public Tensor getQ() {
    return MatrixDotTranspose.of(svd.getU(), svd.getV());
  }

  @Override // from PolarDecomposition
  public Tensor getS() {
    // TODO optimize
    return Dot.of(svd.getV(), DiagonalMatrix.with(svd.values()), Transpose.of(svd.getV()));
  }
}
