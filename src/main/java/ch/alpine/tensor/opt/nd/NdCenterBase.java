// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

public abstract class NdCenterBase implements NdCenterInterface, Serializable {
  private final Tensor center;

  protected NdCenterBase(Tensor center) {
    this.center = Objects.requireNonNull(center);
  }

  @Override // from NdCenterInterface
  public final Scalar distance(CoordinateBoundingBox coordinateBoundingBox) {
    return distance(coordinateBoundingBox.mapInside(center));
  }

  @Override // from NdCenterInterface
  public final boolean lessThan(int dimension, Scalar median) {
    return Scalars.lessThan(center.Get(dimension), median);
  }
}
