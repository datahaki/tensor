// code by Eric Simonton
// adapted by jph and clruch
package ch.ethz.idsc.tensor.opt.nd;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class NdMatch<V> implements Serializable {
  private final Tensor location;
  private final V value;
  private final Scalar distance;

  /** @param location
   * @param value
   * @param distance */
  /* package */ NdMatch(Tensor location, V value, Scalar distance) {
    this.location = location;
    this.value = value;
    this.distance = distance;
  }

  /** @return location in map */
  public Tensor location() {
    return location;
  }

  /** @return value associated to location in map */
  public V value() {
    return value;
  }

  /** @return distance of location to center of cluster */
  public Scalar distance() {
    return distance;
  }
}