// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

public interface NdVisitor<V> {
  /** @param dimension
   * @param mean
   * @return */
  boolean push_leftFirst(int dimension, Scalar mean);

  /** there is a pop invoked for each call to {@link #push_leftFirst(int, Scalar)} */
  void pop();

  /** @param ndBounds
   * @return whether visiting should proceed within the given bounds */
  boolean isViable(NdBounds ndBounds);

  /** @param ndPair */
  void consider(NdPair<V> ndPair);
}
