// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** quickest proximity check to determine whether any point in given map
 * has distance less equals radius from given center.
 * 
 * The search immediately stops after first match is found. */
public class NdInsideRadius<V> extends NdCollectBase<V> {
  /** @param ndMap
   * @param ndCenterInterface
   * @param radius non-negative
   * @return */
  public static <V> boolean anyMatch(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, Scalar radius) {
    NdInsideRadius<V> ndInsideRadius = new NdInsideRadius<>(ndCenterInterface, radius);
    ndMap.visit(ndInsideRadius);
    return ndInsideRadius.found;
  }

  // ---
  private boolean found = false;

  /** @param ndCenterInterface
   * @param radius non-negative */
  protected NdInsideRadius(NdCenterInterface ndCenterInterface, Scalar radius) {
    super(ndCenterInterface, radius);
  }

  @Override // from NdVisitor
  public boolean isViable(CoordinateBoundingBox box) {
    return !found //
        && Scalars.lessEquals(ndCenterInterface.distance(box), radius);
  }

  @Override // from NdVisitor
  public void consider(NdEntry<V> ndEntry) {
    if (!found)
      found = Scalars.lessEquals(ndCenterInterface.distance(ndEntry.location()), radius);
  }
}