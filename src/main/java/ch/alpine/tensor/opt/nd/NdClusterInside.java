// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** quickest proximity check to determine whether any point in given map
 * is within radius from given center. search immediately stops after first
 * match is found. */
public class NdClusterInside<V> extends NdClusterBase<V> {
  /** @param ndMap
   * @param ndCenterInterface
   * @param radius non-negative
   * @return */
  public static <V> boolean anyMatch(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, Scalar radius) {
    NdClusterInside<V> ndClusterInside = new NdClusterInside<>(ndCenterInterface, radius);
    ndMap.visit(ndClusterInside);
    return ndClusterInside.found;
  }

  // ---
  private boolean found = false;

  /** @param ndCenterInterface
   * @param radius non-negative */
  protected NdClusterInside(NdCenterInterface ndCenterInterface, Scalar radius) {
    super(ndCenterInterface, radius);
  }

  @Override // from NdVisitor
  public boolean isViable(NdBox ndBox) {
    return !found //
        && Scalars.lessThan(ndCenterInterface.distance(ndBox), radius);
  }

  @Override // from NdVisitor
  public void consider(NdEntry<V> ndEntry) {
    if (!found)
      found = Scalars.lessThan(ndCenterInterface.distance(ndEntry.location()), radius);
  }
}