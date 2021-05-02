// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

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

  /** @param digits vector
   * @return
   * @throws Exception if input is not a vector */
  public static Scalar of(Tensor digits) {
    return new HornerScheme(digits).apply(TEN);
  }
}
