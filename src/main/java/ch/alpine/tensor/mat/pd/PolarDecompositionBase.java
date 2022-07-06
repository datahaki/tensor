// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensors;

/* package */ abstract class PolarDecompositionBase implements PolarDecomposition {
  @Override // from Object
  public final String toString() {
    return String.format("PolarDecomposition[%s]", Tensors.message(getPositiveSemidefinite(), getUnitary()));
  }
}
