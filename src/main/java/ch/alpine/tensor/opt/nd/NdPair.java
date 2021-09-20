// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;

/* package */ class NdPair<V> implements Serializable {
  private final Tensor location; // <- key
  private final V value;

  /* package */ NdPair(Tensor location, V value) {
    this.location = location;
    this.value = value;
  }

  public Tensor location() {
    return location;
  }

  public V value() {
    return value;
  }
}