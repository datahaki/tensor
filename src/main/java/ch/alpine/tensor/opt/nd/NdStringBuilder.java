// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.tensor.Scalar;

/* package */ class NdStringBuilder<V> implements NdVisitor<V> {
  private final List<String> list = new ArrayList<>();
  private int depth;

  @Override
  public boolean push_firstLo(int dimension, Scalar mean) {
    ++depth;
    return true;
  }

  @Override
  public void pop() {
    --depth;
  }

  @Override
  public boolean isViable(CoordinateBoundingBox box) {
    return true;
  }

  @Override
  public void consider(NdEntry<V> ndEntry) {
    list.add(String.format("@%d %s", depth, ndEntry));
  }

  @Override
  public String toString() {
    return list.toString();
  }
}