// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

public class NdPrint<V> implements NdVisitor<V> {
  private final StringBuilder stringBuilder = new StringBuilder();
  private int depth;

  @Override
  public boolean push_leftFirst(NdBounds ndBounds, int dimension, Scalar mean) {
    ++depth;
    return true;
  }

  @Override
  public void pop() {
    --depth;
  }

  @Override
  public boolean isViable(NdBounds ndBounds) {
    return true;
  }

  @Override
  public void consider(NdPair<V> ndPair) {
    stringBuilder.append(String.format("%s(%s, %s)\n", " ".repeat(depth), ndPair.location(), ndPair.value()));
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}