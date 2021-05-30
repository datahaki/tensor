// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;

import ch.alpine.tensor.Scalar;

public interface NdCluster<V> {
  /** @param ndPair */
  void consider(NdPair<V> ndPair);

  /** @param ndBounds
   * @return */
  boolean isViable(NdBounds ndBounds);

  /** @param dimension
   * @return */
  Scalar center_Get(int dimension);

  /** @return */
  Collection<NdMatch<V>> collection();
}
