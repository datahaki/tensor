// code by gjoel
package ch.alpine.tensor.red;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;

/** InterquartileRange of the tensor library only operates on vectors.
 * 
 * Mathematica::InterquartileRange also operates (trivially) on matrices.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/InterquartileRange.html">InterquartileRange</a> */
public enum InterquartileRange {
  ;
  private static final Scalar LO = Rational.of(1, 4);
  private static final Scalar HI = Rational.of(3, 4);

  /** Example:
   * <code>InterquartileRange[{0, 1, 2, 3, 10}] == 2</code>
   * 
   * @param samples unsorted
   * @return interquartile range as scalar */
  public static Scalar of(Tensor samples) {
    ScalarUnaryOperator scalarUnaryOperator = Quantile.of(VectorQ.require(samples));
    return scalarUnaryOperator.apply(HI).subtract(scalarUnaryOperator.apply(LO));
  }

  /** @param distribution
   * @return interquartile range of given distribution as scalar
   * @throws Exception if given distribution does not implement {@link InverseCDF} */
  public static Scalar of(Distribution distribution) {
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    return inverseCDF.quantile(HI).subtract(inverseCDF.quantile(LO));
  }
}
