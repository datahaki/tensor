// code by Eric Simonton
// adapted by jph and clruch
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class SphericalNdCluster<V> implements NdCollector<V>, Serializable {
  public static <V> NdCollector<V> create(NdCenterInterface ndCenterInterface, Scalar radius) {
    return new SphericalNdCluster<>(ndCenterInterface, radius);
  }

  // ---
  private final NdCenterInterface ndCenterInterface;
  private final Tensor center;
  private final Scalar radius;
  private final List<NdMatch<V>> list = new LinkedList<>();

  /** @param ndCenterInterface
   * @param limit positive */
  private SphericalNdCluster(NdCenterInterface ndCenterInterface, Scalar radius) {
    this.ndCenterInterface = ndCenterInterface;
    this.center = ndCenterInterface.center();
    this.radius = radius;
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

  @Override
  public boolean isViable(NdBounds ndBounds) {
    Tensor test = Tensors.vector(i -> ndBounds.clip(i).apply(center.Get(i)), center.length());
    return Scalars.lessThan(ndCenterInterface.distance(test), radius);
  }

  @Override
  public boolean leftFirst(NdBounds ndBounds, int dimension, Scalar mean) {
    return true;
  }

  @Override
  public Collection<NdMatch<V>> collection() {
    return list;
  }
}