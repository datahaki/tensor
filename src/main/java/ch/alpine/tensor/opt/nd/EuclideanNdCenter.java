// code by jph
package ch.alpine.tensor.opt.nd;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;

public class EuclideanNdCenter implements NdCenterInterface, Serializable {
  /** @param center vector
   * @return */
  public static NdCenterInterface of(Tensor center) {
    return new EuclideanNdCenter(center.copy().unmodifiable());
  }

  /***************************************************/
  private final Tensor center;

  private EuclideanNdCenter(Tensor center) {
    this.center = center;
  }

  @Override // from NdCenterInterface
  public Scalar distance(Tensor vector) {
    return Vector2Norm.between(vector, center);
  }

  @Override // from NdCenterInterface
  public Tensor center() {
    return center;
  }
}
