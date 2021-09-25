// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** distance calculation from a certain coordinate to
 * another point or an axis aligned bounding box. */
public interface NdCenterInterface {
  /** @param point
   * @return distance from center to given point */
  Scalar distance(Tensor point);

  /** @param ndBox
   * @return distance from center to given axis aligned bounding box */
  Scalar distance(NdBox ndBox);

  /** <pre>
   * center[dimension] < median
   * </pre>
   * 
   * @param dimension
   * @param median
   * @return whether coordinate in center with given dimension is less than provided median */
  boolean lessThan(int dimension, Scalar median);
}