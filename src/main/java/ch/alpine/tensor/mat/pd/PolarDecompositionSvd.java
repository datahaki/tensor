// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

/* package */ abstract class PolarDecompositionSvd extends PolarDecompositionBase {
  final SingularValueDecomposition svd;

  public PolarDecompositionSvd(SingularValueDecomposition svd) {
    this.svd = svd;
  }

  @Override // from PolarDecomposition
  public final Tensor getUnitary() {
    return MatrixDotTranspose.of(svd.getU(), svd.getV());
  }
}
