// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

public interface NdVisitor<V> {
  /** @param ndBounds
   * @param dimension
   * @param mean
   * @return */
  boolean push_leftFirst(NdBounds ndBounds, int dimension, Scalar mean);

  void pop();

  /** @param ndBounds
   * @return */
  boolean isViable(NdBounds ndBounds);

  /** @param ndPair */
  void consider(NdPair<V> ndPair);
}
