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

  /** The function provides a lower bound for the distances from the center to any point
   * from the given box, i.e. the following inequality has to hold
   * <pre>
   * distance(box) <= distance(any point from box)
   * </pre>
   * 
   * In particular, if the center of this instance is located inside the given box, then
   * the function returns zero.
   * 
   * @param box
   * @return distance from center to given axis aligned bounding box */
  Scalar distance(CoordinateBoundingBox box);

  /** <pre>
   * center[dimension] < median
   * </pre>
   * 
   * @param dimension
   * @param median
   * @return whether coordinate in center with given dimension is less than provided median */
  boolean lessThan(int dimension, Scalar median);
}