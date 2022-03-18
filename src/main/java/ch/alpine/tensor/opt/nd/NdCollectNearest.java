// code by Eric Simonton
// adapted by jph, clruch
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Integers;

/** to obtain k-nearest neighbors from given center
 * 
 * Applications: rrts motion planning */
public class NdCollectNearest<V> implements NdVisitor<V> {
  private static final Comparator<NdMatch<?>> COMPARATOR = //
      (o1, o2) -> Scalars.compare(o2.distance(), o1.distance());

  /** @param ndMap
   * @param ndCenterInterface
   * @param limit strictly positive
   * @return */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, int limit) {
    NdCollectNearest<V> ndCollectNearest = new NdCollectNearest<>(ndCenterInterface, limit);
    ndMap.visit(ndCollectNearest);
    return ndCollectNearest.queue();
  }

  /** @param ndMap
   * @param ndCenterInterface
   * @return nearest match in map with respect to given center interface, or null if ndMap is empty */
  public static <V> NdMatch<V> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface) {
    NdCollectNearest<V> ndCollectNearest = new NdCollectNearest<>(ndCenterInterface, 1);
    ndMap.visit(ndCollectNearest);
    return ndCollectNearest.queue().poll();
  }

  // ---
  private final NdCenterInterface ndCenterInterface;
  private final int limit;
  private final Queue<NdMatch<V>> queue;

  /** @param ndCenterInterface
   * @param limit strictly positive */
  protected NdCollectNearest(NdCenterInterface ndCenterInterface, int limit) {
    this.ndCenterInterface = Objects.requireNonNull(ndCenterInterface);
    this.limit = Integers.requirePositive(limit);
    queue = new PriorityQueue<>(COMPARATOR);
  }

  @Override // from NdVisitor
  public boolean push_firstLo(int dimension, Scalar median) {
    return ndCenterInterface.lessThan(dimension, median);
  }

  @Override // from NdVisitor
  public void pop() {
    // ---
  }

  @Override // from NdVisitor
  public boolean isViable(CoordinateBoundingBox coordinateBoundingBox) {
    return queue.size() < limit //
        || Scalars.lessThan(ndCenterInterface.distance(coordinateBoundingBox), queue.peek().distance());
  }

  @Override // from NdVisitor
  public void consider(NdEntry<V> ndEntry) {
    NdMatch<V> ndMatch = new NdMatch<>(ndEntry, ndCenterInterface.distance(ndEntry.location()));
    if (queue.size() < limit)
      queue.add(ndMatch);
    else
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