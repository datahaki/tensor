// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

public class NdClusterRadius<V> implements NdVisitor<V> {
  /** @param ndMap
   * @param ndCenterInterface
   * @param radius non-negative
   * @return */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, Scalar radius) {
    NdClusterRadius<V> sphericalNdCluster = new NdClusterRadius<>(ndCenterInterface, radius);
    ndMap.visit(sphericalNdCluster);
    return sphericalNdCluster.list();
  }

  // ---
  private final NdCenterInterface ndCenterInterface;
  private final Tensor center;
  private final Scalar radius;
  private final List<NdMatch<V>> list = new LinkedList<>();

  /** @param ndCenterInterface
   * @param radius non-negative */
  protected NdClusterRadius(NdCenterInterface ndCenterInterface, Scalar radius) {
    this.ndCenterInterface = ndCenterInterface;
    this.center = ndCenterInterface.center();
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from NdVisitor
  public boolean push_leftFirst(int dimension, Scalar median) {
    return true;
  }

  @Override // from NdVisitor
  public void pop() {
    // ---
  }

  @Override // from NdVisitor
  public boolean isViable(NdBox ndBox) {
    return Scalars.lessThan(ndCenterInterface.distance(ndBox.clip(center)), radius);
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