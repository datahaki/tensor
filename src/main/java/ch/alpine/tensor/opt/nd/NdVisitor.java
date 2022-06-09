// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

public interface NdVisitor<V> {
  /** @param dimension
   * @param median
   * @return whether lo half should be visited before hi half */
  boolean push_firstLo(int dimension, Scalar median);

  /** a pop is invoked for each call to {@link #push_firstLo(int, Scalar)} */
  void pop();

  /** @param coordinateBoundingBox
   * @return whether visiting should proceed within the given bounds */
  boolean isViable(CoordinateBoundingBox coordinateBoundingBox);

  /** @param ndEntry */
  void consider(NdEntry<V> ndEntry);
}
