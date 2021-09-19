// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** function returns the queue size of leaf nodes in the tree.
 * use the function to determine if the tree is well balanced.
 * 
 * <p>Example use:
 * <pre>
 * Tally.sorted(Flatten.of(ndTreeMap.binSize()))
 * </pre> */
public class NdBinsize<V> implements NdVisitor<V> {
  private final Tensor bins = Tensors.empty();
  int count = 0;

  @Override
  public boolean push_leftFirst(NdBounds ndBounds, int dimension, Scalar mean) {
    return true;
  }

  @Override
  public void pop() {
    bins.append(RealScalar.of(count));
    count = 0;
  }

  @Override
  public boolean isViable(NdBounds ndBounds) {
    return true;
  }

  @Override
  public void consider(NdPair<V> ndPair) {
    ++count;
  }

  public Tensor bins() {
    return bins;
  }
}
