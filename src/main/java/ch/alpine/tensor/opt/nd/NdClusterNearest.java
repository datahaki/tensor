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
import ch.alpine.tensor.ext.Integers;

public class NdClusterNearest<V> implements NdVisitor<V> {
  private static final Comparator<NdMatch<?>> COMPARATOR = //
      (o1, o2) -> Scalars.compare(o2.distance(), o1.distance());

  /** @param ndMap
   * @param ndCenterInterface
   * @param limit strictly positive
   * @return */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, int limit) {
    NdClusterNearest<V> ndClusterNearest = new NdClusterNearest<>(ndCenterInterface, limit);
    ndMap.visit(ndClusterNearest);
    return ndClusterNearest.queue();
  }

  /** @param ndMap
   * @param ndCenterInterface
   * @return nearest match in map with respect to given center interface, or null if ndMap is empty */
  public static <V> NdMatch<V> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface) {
    NdClusterNearest<V> ndClusterNearest = new NdClusterNearest<>(ndCenterInterface, 1);
    ndMap.visit(ndClusterNearest);
    return ndClusterNearest.queue().poll();
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
    this.limit = Integers.requirePositive(limit);
    queue = new PriorityQueue<>(COMPARATOR);
  }

  @Override // from NdVisitor
  public boolean push_leftFirst(int dimension, Scalar median) {
    return Scalars.lessThan(center.Get(dimension), median);
  }

  @Override // from NdVisitor
  public void pop() {
    // ---
  }

  @Override // from NdVisitor
  public boolean isViable(NdBox ndBox) {
    return queue.size() < limit //
        || Scalars.lessThan(ndCenterInterface.distance(ndBox.clip(center)), queue.peek().distance());
  }

  @Override // from NdVisitor
  public void consider(NdEntry<V> ndEntry) {
    NdMatch<V> ndMatch = new NdMatch<>(ndEntry, ndCenterInterface.distance(ndEntry.location()));
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