// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Tensor;

/** NdMap contains (coordinate, value)-pairs.
 * multiple values can be associated to the same coordinate. */
public interface NdMap<V> {
  /** insert (location, value)-pair to nd map
   * 
   * @param location
   * @param value
   * @throws Exception if given location is not inside permitted region */
  void insert(Tensor location, V value);

  /** @return number of entries stored in map */
  int size();

  /** @return true if size() == 0 */
  boolean isEmpty();

  /** @param ndVisitor */
  void visit(NdVisitor<V> ndVisitor);
}
