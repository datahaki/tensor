// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;

/** Remark: The sum of the leverages equals the rank of the design matrix.
 * 
 * @see InfluenceMatrix
 * @see Mahalanobis */
public interface LeveragesInterface {
  /** @return vector with diagonal entries of influence matrix each of which are guaranteed
   * to be in the unit interval [0, 1] */
  Tensor leverages();

  /** @return sqrt of leverages identical to Mahalanobis distance guaranteed to be in the unit
   * interval [0, 1] */
  Tensor leverages_sqrt();
}
