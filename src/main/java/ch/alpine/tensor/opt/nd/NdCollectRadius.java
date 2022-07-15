// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** for collecting points that are up to radius away from given center.
 * 
 * Applications: dbscan
 * 
 * @see NdInsideRadius */
public class NdCollectRadius<V> extends NdCollectBase<V> {
  /** @param ndMap
   * @param ndCenterInterface
   * @param radius non-negative
   * @return collection of matches in no particular order
   * @throws Exception if given radius is negative */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, Scalar radius) {
    NdCollectRadius<V> ndCollectRadius = new NdCollectRadius<>(ndCenterInterface, radius);
    ndMap.visit(ndCollectRadius);
    return ndCollectRadius.list();
  }

  // ---
  private final List<NdMatch<V>> list = new LinkedList<>();

  /** @param ndCenterInterface
   * @param radius non-negative */
  protected NdCollectRadius(NdCenterInterface ndCenterInterface, Scalar radius) {
    super(ndCenterInterface, radius);
  }

  @Override // from NdVisitor
  public boolean isViable(CoordinateBoundingBox coordinateBoundingBox) {
    return Scalars.lessEquals(ndCenterInterface.distance(coordinateBoundingBox), radius);
  }

  @Override // from NdVisitor
  public void consider(NdEntry<V> ndEntry) {
    Scalar distance = ndCenterInterface.distance(ndEntry.location());
    if (Scalars.lessEquals(distance, radius))
      list.add(new NdMatch<>(ndEntry, distance));
  }

  /** @return all matches */
  public List<NdMatch<V>> list() {
    return list;
  }
}
