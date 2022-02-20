// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.Serializable;

import ch.alpine.tensor.Tensors;

/* package */ abstract class PolarDecompositionBase implements PolarDecomposition, Serializable {
  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", //
        PolarDecomposition.class.getSimpleName(), //
        Tensors.message(getPositiveSemidefinite(), getUnitary()));
  }
}
