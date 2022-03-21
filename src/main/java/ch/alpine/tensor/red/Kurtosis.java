// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.KurtosisInterface;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Kurtosis.html">Kurtosis</a> */
public enum Kurtosis {
  ;
  private static final Scalar _2 = RealScalar.of(2);
  private static final Scalar _4 = RealScalar.of(4);

  /** @param vector
   * @return */
  public static Scalar of(Tensor vector) {
    Scalar variance = CentralMoment.of(vector, _2);
    return CentralMoment.of(vector, _4).divide(variance).divide(variance);
  }

  /** @param distribution
   * @return CentralMoment[pdf, 4] / CentralMoment[pdf, 2]^2 */
  public static Scalar of(Distribution distribution) {
    if (distribution instanceof KurtosisInterface)
      return ((KurtosisInterface) distribution).kurtosis();
    Scalar variance = Variance.of(distribution);
    return CentralMoment.of(distribution, 4).divide(variance).divide(variance);
  }
}
