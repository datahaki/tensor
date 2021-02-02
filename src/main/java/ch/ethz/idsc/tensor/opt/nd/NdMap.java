// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.util.Collection;

import ch.ethz.idsc.tensor.Tensor;

/** NdMap contains (coordinate, value)-pairs.
 * multiple values can be associated to the same coordinate.
 * 
 * @param <V> */
public interface NdMap<V> {
  /** @param location
   * @param value */
  void add(Tensor location, V value);

  /** @return number of entries stored in map */
  int size();

  /** @return true if size() == 0 */
  boolean isEmpty();

  /** clears all entries from map */
  void clear();

  /** @param ndCenter
   * @param limit strictly positive
   * @return cluster of at most limit closest points to given ndCenter. The application
   * layer should not make assumptions on the ordering of the points in the cluster. */
  Collection<NdEntry<V>> cluster(NdCenterInterface ndCenter, int limit);
}
