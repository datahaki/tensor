// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensors;

/* package */ abstract class PolarDecompositionBase implements PolarDecomposition {
  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", //
        PolarDecomposition.class.getSimpleName(), //
        Tensors.message(getPositiveSemidefinite(), getUnitary()));
  }
}
