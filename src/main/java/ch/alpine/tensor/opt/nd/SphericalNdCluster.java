// code by jph
package ch.alpine.tensor.opt.nd;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Sign;

public class SphericalNdCluster<V> implements NdVisitor<V> {
  /** @param ndMap
   * @param ndCenterInterface
   * @param radius
   * @return */
  public static <V> Collection<NdMatch<V>> of(NdMap<V> ndMap, NdCenterInterface ndCenterInterface, Scalar radius) {
    SphericalNdCluster<V> sphericalNdCluster = new SphericalNdCluster<>(ndCenterInterface, radius);
    ndMap.visit(sphericalNdCluster);
    return sphericalNdCluster.list;
  }

  // ---
  private final NdCenterInterface ndCenterInterface;
  private final Tensor center;
  private final Scalar radius;
  private final List<NdMatch<V>> list = new LinkedList<>();

  /** @param ndCenterInterface
   * @param radius non-negative */
  protected SphericalNdCluster(NdCenterInterface ndCenterInterface, Scalar radius) {
    this.ndCenterInterface = ndCenterInterface;
    this.center = ndCenterInterface.center();
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override
  public boolean push_leftFirst(NdBounds ndBounds, int dimension, Scalar mean) {
    return true;
  }

  @Override
  public void pop() {
    // ---
  }

  @Override
  public boolean isViable(NdBounds ndBounds) {
    // TODO justify this computation!?
    Tensor test = Tensors.vector(i -> ndBounds.clip(i).apply(center.Get(i)), center.length());
    return Scalars.lessThan(ndCenterInterface.distance(test), radius);
  }

  @Override
  public void consider(NdPair<V> ndPair) {
    Scalar distance = ndCenterInterface.distance(ndPair.location());
    if (Scalars.lessThan(distance, radius))
      list.add(new NdMatch<>( //
          ndPair.location(), //
          ndPair.value(), //
          distance));
  }

  public List<NdMatch<V>> list() {
    return list;
  }
}