// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;

/** immutable */
public class NdEntry<V> implements Serializable {
  private final Tensor location;
  private final V value;

  /* package */ NdEntry(Tensor location, V value) {
    this.location = location;
    this.value = value;
  }

  /** @return location as key */
  public Tensor location() {
    return location;
  }

  /** @return value */
  public V value() {
    return value;
  }

  @Override // from Object
  public String toString() {
    return String.format("NdEntry[%s, %s]", location, value);
  }
}