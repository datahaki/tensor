// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.KurtosisInterface;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Kurtosis.html">Kurtosis</a> */
public enum Kurtosis {
  ;
  /** @param vector
   * @return */
  public static Scalar of(Tensor vector) {
    Scalar cm2 = CentralMoment.of(vector, 2);
    return CentralMoment.of(vector, 4).divide(cm2).divide(cm2);
  }

  /** @param distribution
   * @return CentralMoment[pdf, 4] / CentralMoment[pdf, 2]^2 */
  public static Scalar of(Distribution distribution) {
    if (distribution instanceof KurtosisInterface kurtosisInterface)
      return kurtosisInterface.kurtosis();
    Scalar variance = Variance.of(distribution);
    return CentralMoment.of(distribution, 4).divide(variance).divide(variance);
  }
}
