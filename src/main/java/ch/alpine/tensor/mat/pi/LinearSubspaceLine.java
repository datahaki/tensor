// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.MathematicaFormat;

enum LinearSubspaceLine implements LinearSubspace {
  INSTANCE;

  @Override // from LinearSubspace
  public Tensor basis() {
    return Tensors.vector(1);
  }

  @Override // from LinearSubspace
  public Scalar projection(Tensor v) {
    return (Scalar) v;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("LinearSubspace", basis());
  }
}
