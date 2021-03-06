// code by Eric Simonton
// adapted by jph and clruch
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ class NdCluster<V> {
  private final NdCenterInterface ndCenterInterface;
  private final Tensor center;
  private final int limit;
  private final Queue<NdMatch<V>> queue;

  /** @param ndCenterInterface
   * @param limit positive */
  public NdCluster(NdCenterInterface ndCenterInterface, int limit) {
    this.ndCenterInterface = ndCenterInterface;
    this.center = ndCenterInterface.center();
    this.limit = limit;
    queue = new PriorityQueue<>(NdMatchComparators.DECREASING);
  }

  public void consider(NdPair<V> ndPair) {
    NdMatch<V> ndEntry = new NdMatch<>( //
        ndPair.location(), //
        ndPair.value(), //
        ndCenterInterface.distance(ndPair.location()));
    if (queue.size() < limit)
      queue.add(ndEntry);
    else //
    if (Scalars.lessThan(ndEntry.distance(), queue.peek().distance())) {
      queue.poll();
      queue.add(ndEntry);
    }
  }

  public boolean isViable(NdBounds ndBounds) {
    if (queue.size() < limit)
      return true;
    Tensor test = Tensors.vector(i -> ndBounds.clip(i).apply(center.Get(i)), center.length());
    return Scalars.lessThan(ndCenterInterface.distance(test), queue.peek().distance());
  }

  public Collection<NdMatch<V>> collection() {
    return queue;
  }

  public Scalar center_Get(int dimension) {
    return center.Get(dimension);
  }
}