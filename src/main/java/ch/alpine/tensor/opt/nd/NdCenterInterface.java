// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public interface NdCenterInterface {
  /** @return center */
  Tensor center();

  /** @param point
   * @return distance from center to point */
  Scalar distance(Tensor point);
}