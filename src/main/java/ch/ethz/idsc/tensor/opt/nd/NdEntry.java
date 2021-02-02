// code by Eric Simonton
// adapted by jph and clruch
package ch.ethz.idsc.tensor.opt.nd;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class NdEntry<V> implements Serializable {
  private static final long serialVersionUID = -4046189014254206016L;
  // ---
  private final Tensor location;
  private final V value;
  private final Scalar distance;

  /* package */ NdEntry(Tensor location, V value, Scalar distance) {
    this.location = location;
    this.value = value;
    this.distance = distance;
  }

  /** @return location of pair in map */
  public Tensor location() {
    return location;
  }

  /** @return value of pair in map */
  public V value() {
    return value;
  }

  /** @return distance of pair to center */
  public Scalar distance() {
    return distance;
  }
}