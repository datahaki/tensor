// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

public class EuclideanNdCenter implements NdCenterInterface, Serializable {
  private static final long serialVersionUID = -8327079294807945414L;

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
