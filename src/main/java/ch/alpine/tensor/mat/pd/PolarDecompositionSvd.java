// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

/* package */ abstract class PolarDecompositionSvd implements PolarDecomposition, Serializable {
  final SingularValueDecomposition svd;

  public PolarDecompositionSvd(SingularValueDecomposition svd) {
    this.svd = svd;
  }

  @Override // from PolarDecomposition
  public final Tensor getUnitary() {
    return MatrixDotTranspose.of(svd.getU(), svd.getV());
  }

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", PolarDecomposition.class.getSimpleName(), Tensors.message(getPositiveSemidefinite(), getUnitary()));
  }
}
