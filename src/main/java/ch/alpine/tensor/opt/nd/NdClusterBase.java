// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Sign;

public abstract class NdClusterBase<V> implements NdVisitor<V> {
  protected final NdCenterInterface ndCenterInterface;
  protected final Scalar radius;

  public NdClusterBase(NdCenterInterface ndCenterInterface, Scalar radius) {
    this.ndCenterInterface = Objects.requireNonNull(ndCenterInterface);
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
}
