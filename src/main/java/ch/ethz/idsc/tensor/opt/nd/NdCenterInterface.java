// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface NdCenterInterface {
  /** @return center */
  Tensor center();

  /** @param point
   * @return distance from center to point */
  Scalar distance(Tensor point);
}