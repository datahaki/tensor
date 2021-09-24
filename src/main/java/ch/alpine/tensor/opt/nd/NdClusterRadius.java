// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

public class NdClusterRadius<V> extends NdClusterBase<V> {
  /** @param ndMap
   * @param ndCenterInterface
   * @param radius non-negative
   * @return */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, Scalar radius) {
    NdClusterRadius<V> ndClusterRadius = new NdClusterRadius<>(ndCenterInterface, radius);
    ndMap.visit(ndClusterRadius);
    return ndClusterRadius.list();
  }

  // ---
  private final List<NdMatch<V>> list = new LinkedList<>();

  /** @param ndCenterInterface
   * @param radius non-negative */
  protected NdClusterRadius(NdCenterInterface ndCenterInterface, Scalar radius) {
    super(ndCenterInterface, radius);
  }

  @Override // from NdVisitor
  public boolean isViable(NdBox ndBox) {
    return Scalars.lessThan(ndCenterInterface.distance(ndBox), radius);
  }

  @Override // from NdVisitor
  public void consider(NdEntry<V> ndEntry) {
    Scalar distance = ndCenterInterface.distance(ndEntry.location());
    if (Scalars.lessThan(distance, radius))
      list.add(new NdMatch<>(ndEntry, distance));
  }

  /** @return all matches */
  public List<NdMatch<V>> list() {
    return list;
  }
}