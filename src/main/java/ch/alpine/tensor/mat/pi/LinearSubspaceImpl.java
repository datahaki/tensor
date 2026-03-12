// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.MathematicaFormat;

class LinearSubspaceImpl implements LinearSubspace {
  /** pinv has dimensions rows x cols with rows <= cols */
  private final Tensor pinv;
  private final Tensor basis;

  public LinearSubspaceImpl(Tensor nullSpace, Tensor basis) {
    this.pinv = PseudoInverse.of(Transpose.of(nullSpace));
    this.basis = basis.unmodifiable();
  }

  @Override
  public Tensor basis() {
    return basis;
  }

  @Override
  public Tensor projection(Tensor v) {
    return apply(pinv.dot(Flatten.of(v)));
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("LinearSubspace", basis());
  }
}
