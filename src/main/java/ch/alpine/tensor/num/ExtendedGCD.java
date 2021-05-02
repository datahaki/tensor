// code by jph
package ch.alpine.tensor.num;

import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** ExtendedGCD is not used inside the tensor library
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ExtendedGCD.html">ExtendedGCD</a> */
public interface ExtendedGCD {
  /** @param one
   * @return */
  static Function<Tensor, ExtendedGCD> function() {
    return new ExtendedGCDWrap()::function;
  }

  /** @return greatest common divider */
  Scalar gcd();

  /** @return vector of factors so that vector . factors() == gcd() */
  Tensor factors();
}
