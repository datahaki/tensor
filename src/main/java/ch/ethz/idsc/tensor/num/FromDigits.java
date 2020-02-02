// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Series;

/** Implementation is consistent with Mathematica
 * 
 * FromDigits[{3, 2, 4}] == 324
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FromDigits.html">FromDigits</a>
 * 
 * @see IntegerDigits */
public enum FromDigits {
  ;
  private static final Scalar TEN = RealScalar.of(10);

  /** @param digits
   * @return */
  public static Scalar of(Tensor digits) {
    return Series.of(Reverse.of(digits)).apply(TEN);
  }
}
