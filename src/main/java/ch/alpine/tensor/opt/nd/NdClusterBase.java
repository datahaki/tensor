// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

public abstract class NdClusterBase<V> implements NdVisitor<V> {
  protected final NdCenterInterface ndCenterInterface;
  protected final Tensor center;
  protected final Scalar radius;

  public NdClusterBase(NdCenterInterface ndCenterInterface, Scalar radius) {
    this.ndCenterInterface = ndCenterInterface;
    this.center = ndCenterInterface.center();
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from NdVisitor
  public final boolean push_leftFirst(int dimension, Scalar median) {
    return true;
  }

  @Override // from NdVisitor
  public final void pop() {
    // ---
  }

  protected final boolean isWithin(Tensor vector) {
    return Scalars.lessThan(ndCenterInterface.distance(vector), radius);
  }
}
