// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

/* package */ class NdStringBuilder<V> implements NdVisitor<V> {
  private final StringBuilder stringBuilder = new StringBuilder();
  private int depth;

  @Override
  public boolean push_leftFirst(int dimension, Scalar mean) {
    ++depth;
    return true;
  }

  @Override
  public void pop() {
    --depth;
  }

  @Override
  public boolean isViable(NdBox ndBox) {
    return true;
  }

  @Override
  public void consider(NdEntry<V> ndEntry) {
    stringBuilder.append(String.format("%s(%s, %s)\n", " ".repeat(depth), ndEntry.location(), ndEntry.value()));
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}