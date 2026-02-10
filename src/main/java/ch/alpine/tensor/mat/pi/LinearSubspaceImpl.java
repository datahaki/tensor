// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.MathematicaFormat;

/* package */ class LinearSubspaceImpl implements LinearSubspace {
  /** pinv has dimensions rows x cols with rows <= cols */
  private final Tensor pinv;
  private final Tensor basis;

  public LinearSubspaceImpl(Tensor nullSpace, Tensor basis) {
    this.pinv = PseudoInverse.of(Transpose.of(nullSpace));
    this.basis = basis;
  }

  @Override
  public Tensor apply(Tensor weights) {
    return weights.dot(basis);
  }

  @Override
  public Tensor basis() {
    return basis;
  }

  @Override
  public Tensor projection(Tensor v) {
    return apply(pinv.dot(Flatten.of(v)));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("LinearSubspace", basis());
  }
}
