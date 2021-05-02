// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sqrt;

/** implementation is consistent with Mathematica::StandardDeviation
 * 
 * Application in Normalize
 * <pre>
 * Normalize.with(StandardDeviation::ofVector)
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/StandardDeviation.html">StandardDeviation</a> */
public enum StandardDeviation {
  ;
  /** @param vector
   * @return square root of variance
   * @throws Exception if input is not a vector, or the input has insufficient length */
  public static Scalar ofVector(Tensor vector) {
    return Sqrt.FUNCTION.apply(Variance.ofVector(vector));
  }
}
