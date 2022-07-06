// code by Eric Simonton
// adapted by jph, clruch
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;

public class NdMatch<V> implements Serializable {
  private final NdEntry<V> ndEntry;
  private final Scalar distance;

  /** @param ndEntry
   * @param distance */
  /* package */ NdMatch(NdEntry<V> ndEntry, Scalar distance) {
    this.ndEntry = ndEntry;
    this.distance = distance;
  }

  /** @return location in map */
  public Tensor location() {
    return ndEntry.location();
  }

  /** @return value associated to location in map */
  public V value() {
    return ndEntry.value();
  }

  /** @return distance of location to center of cluster */
  public Scalar distance() {
    return distance;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("NdMatch", ndEntry, distance);
  }
}