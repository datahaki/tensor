// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;

public interface NdVisitor<V> {
  /** @param ndPair */
  void consider(NdPair<V> ndPair);

  /** @param ndBounds
   * @return */
  boolean isViable(NdBounds ndBounds);

  boolean leftFirst(NdBounds ndBounds, int dimension, Scalar mean);
}
