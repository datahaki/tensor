// code by jph
package ch.alpine.tensor.mat.pi;

import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.MathematicaFormat;

/* package */ record LinearSubspaceNull(List<Integer> size) implements LinearSubspace {
  @Override
  public Tensor apply(Tensor weights) {
    Throw.unless(Tensors.empty().equals(weights));
    return ConstantArray.of(RealScalar.ZERO, size);
  }

  @Override // from LinearSubspace
  public Tensor basis() {
    return Tensors.empty();
  }

  @Override // from LinearSubspace
  public Tensor projection(Tensor v) {
    Dimensions dimensions = new Dimensions(v);
    Throw.unless(dimensions.isArrayWith(size::equals));
    return v.maps(Scalar::zero);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("LinearSubspace", 0, size);
  }
}
