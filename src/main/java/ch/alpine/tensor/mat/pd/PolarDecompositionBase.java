// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.io.MathematicaFormat;

/* package */ abstract class PolarDecompositionBase implements PolarDecomposition {
  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise("PolarDecomposition", getPositiveSemidefinite(), getUnitary());
  }
}
