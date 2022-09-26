// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Sign;

public abstract class NdCollectBase<V> implements NdVisitor<V> {
  protected final NdCenterInterface ndCenterInterface;
  protected final Scalar radius;

  protected NdCollectBase(NdCenterInterface ndCenterInterface, Scalar radius) {
    this.ndCenterInterface = Objects.requireNonNull(ndCenterInterface);
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from NdVisitor
  public boolean push_firstLo(int dimension, Scalar median) {
    return true;
  }

  @Override // from NdVisitor
  public void pop() {
    // ---
  }
}
