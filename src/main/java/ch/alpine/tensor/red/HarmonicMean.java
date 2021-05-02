// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** implementation is consistent with Mathematica::HarmonicMean
 * for vector input, but not for matrix input.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HarmonicMean.html">HarmonicMean</a> */
public enum HarmonicMean {
  ;
  /** computes the harmonic mean of the {@link Scalar}s on the first level of given tensor.
   * 
   * @param vector of non-zero scalars
   * @return harmonic mean of entries in given vector
   * @throws ArithmeticException if any entry of vector is zero, or vector is empty */
  public static Scalar ofVector(Tensor vector) {
    return RealScalar.of(vector.length()) //
        .divide(Total.ofVector(vector.map(Scalar::reciprocal)));
  }
}
