// code by Eric Simonton
// adapted by jph and clruch
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class NdClusterNearest<V> implements NdVisitor<V> {
  private static final Comparator<NdMatch<?>> COMPARATOR = //
      (o1, o2) -> Scalars.compare(o2.distance(), o1.distance());

  /** @param ndMap
   * @param ndCenterInterface
   * @param limit strictly positive
   * @return */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, int limit) {
    NdClusterNearest<V> nearestNdCluster = new NdClusterNearest<>(ndCenterInterface, limit);
    ndMap.visit(nearestNdCluster);
    return nearestNdCluster.queue();
  }

  // ---
  private final NdCenterInterface ndCenterInterface;
  private final Tensor center;
  private final int limit;
  private final Queue<NdMatch<V>> queue;

  /** @param ndCenterInterface
   * @param limit strictly positive */
  protected NdClusterNearest(NdCenterInterface ndCenterInterface, int limit) {
    this.ndCenterInterface = ndCenterInterface;
    this.center = ndCenterInterface.center();
    this.limit = limit;
    if (limit < 1)
      throw new IllegalArgumentException("limit must be positive but is " + limit);
    queue = new PriorityQueue<>(COMPARATOR);
  }

  @Override // from NdVisitor
  public boolean push_leftFirst(int dimension, Scalar mean) {
    return Scalars.lessThan(center.Get(dimension), mean);
  }

  @Override // from NdVisitor
  public void pop() {
    // ---
  }

  @Override // from NdVisitor
  public boolean isViable(NdBounds ndBounds) {
    if (queue.size() < limit)
      return true;
    Tensor test = Tensors.vector(i -> ndBounds.clip(i).apply(center.Get(i)), center.length());
    return Scalars.lessThan(ndCenterInterface.distance(test), queue.peek().distance());
  }

  @Override // from NdVisitor
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

  /** @return priority queue */
  public Queue<NdMatch<V>> queue() {
    return queue;
  }
}