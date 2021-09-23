// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

public interface NdVisitor<V> {
  /** @param dimension
   * @param median
   * @return */
  boolean push_leftFirst(int dimension, Scalar median);

  /** there is a pop invoked for each call to {@link #push_leftFirst(int, Scalar)} */
  void pop();

  /** @param ndBox
   * @return whether visiting should proceed within the given bounds */
  boolean isViable(NdBox ndBox);

  /** @param ndPair */
  void consider(NdPair<V> ndPair);
}
