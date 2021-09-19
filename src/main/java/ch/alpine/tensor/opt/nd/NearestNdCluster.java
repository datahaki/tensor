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

public class NearestNdCluster<V> implements NdVisitor<V> {
  /** @param <V>
   * @param ndCenterInterface
   * @param limit
   * @return */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, int limit) {
    NearestNdCluster<V> nearestNdCluster = new NearestNdCluster<>(ndCenterInterface, limit);
    ndMap.visit(nearestNdCluster);
    return nearestNdCluster.queue;
  }

  // ---
  private final NdCenterInterface ndCenterInterface;
  private final Tensor center;
  private final int limit;
  private final Queue<NdMatch<V>> queue;

  /** @param ndCenterInterface
   * @param limit positive */
  protected NearestNdCluster(NdCenterInterface ndCenterInterface, int limit) {
    this.ndCenterInterface = ndCenterInterface;
    this.center = ndCenterInterface.center();
    this.limit = limit;
    queue = new PriorityQueue<>(NdMatchComparators.DECREASING);
  }

  @Override
  public void consider(NdPair<V> ndPair) {
    NdMatch<V> ndMatch = new NdMatch<>( //
        ndPair.location(), //
        ndPair.value(), //
        ndCenterInterface.distance(ndPair.location()));
    if (queue.size() < limit)
      queue.add(ndMatch);
    else //
    if (Scalars.lessThan(ndMatch.distance(), queue.peek().distance())) {
      queue.poll();
      queue.add(ndMatch);
    }
  }

  @Override
  public boolean isViable(NdBounds ndBounds) {
    if (queue.size() < limit)
      return true;
    Tensor test = Tensors.vector(i -> ndBounds.clip(i).apply(center.Get(i)), center.length());
    return Scalars.lessThan(ndCenterInterface.distance(test), queue.peek().distance());
  }

  @Override
  public boolean leftFirst(NdBounds ndBounds, int dimension, Scalar mean) {
    return Scalars.lessThan(center.Get(dimension), mean);
  }
}