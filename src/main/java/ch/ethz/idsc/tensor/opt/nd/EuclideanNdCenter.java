// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Norm;

public class EuclideanNdCenter implements NdCenterInterface, Serializable {
  private static final long serialVersionUID = 4287308934962123171L;

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

  @Override // from VectorNormInterface
  public Scalar ofVector(Tensor vector) {
    return Norm._2.between(vector, center);
  }

  @Override // from NdCenterInterface
  public Tensor center() {
    return center;
  }
}
