// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

public class NdPrint<V> implements NdVisitor<V> {
  private final StringBuilder stringBuilder = new StringBuilder();

  @Override
  public void consider(NdPair<V> ndPair) {
    stringBuilder.append(String.format("(%s, %s)\n", ndPair.location(), ndPair.value()));
  }

  @Override
  public boolean isViable(NdBounds ndBounds) {
    return true;
  }

  @Override
  public boolean leftFirst(NdBounds ndBounds, int dimension, Scalar mean) {
    return true;
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}