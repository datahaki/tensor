// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Mean.html">Mean</a> */
public enum Mean {
  ;
  /** computes the mean of the entries on the first level of given tensor.
   * The return value has {@link Dimensions} of input tensor reduced by 1.
   * 
   * <p>For instance,
   * <ul>
   * <li>for a vector of scalars, the mean is a {@link Scalar}
   * <li>for a matrix, the function returns a the average of rows as a vector
   * </ul>
   * 
   * <p>Careful: Mean.of({}) throws an exception.
   * In Mathematica, Mean[{}] is undefined.
   * 
   * @param tensor non-empty
   * @return average of entries in tensor
   * @throws ArithmeticException if tensor is empty
   * @throws Throw if tensor is a {@link Scalar} */
  public static Tensor of(Tensor tensor) {
    return Total.of(tensor).divide(RealScalar.of(tensor.length()));
  }

  /** in practice, the mean of a vector is the most common use
   * 
   * @param vector
   * @return scalar that is the arithmetic mean of the entries in given vector
   * @see Total#ofVector(Tensor)
   * @see Variance#ofVector(Tensor) */
  public static Scalar ofVector(Tensor vector) {
    return (Scalar) of(vector);
  }

  /** @param distribution
   * @return mean of given probability distribution */
  public static Scalar of(Distribution distribution) {
    return Expectation.mean(distribution);
  }
}
