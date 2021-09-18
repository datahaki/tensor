// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;

import ch.alpine.tensor.Tensor;

/** NdMap contains (coordinate, value)-pairs.
 * multiple values can be associated to the same coordinate. */
public interface NdMap<V> {
  /** function adds pair (location, value) to map.
   * The size of the map is incremented by one.
   * No element that already exists in the map will be removed.
   * 
   * @param location
   * @param value */
  void add(Tensor location, V value);

  /** @return number of entries stored in map */
  int size();

  /** @return true if size() == 0 */
  boolean isEmpty();

  /** @param ndCenterInterface
   * @param limit strictly positive
   * @return cluster of at most limit closest points to given ndCenter. The application
   * layer should not make assumptions on the ordering of the points in the cluster. */
  Collection<NdMatch<V>> cluster(NdCluster<V> ndCluster);
}
