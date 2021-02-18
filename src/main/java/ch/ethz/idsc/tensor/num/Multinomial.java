// code by jph
package ch.ethz.idsc.tensor.num;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Factorial;

/** Implementation does not support negative input.
 * 
 * Otherwise, implementation is consistent with Mathematica, in particular
 * <pre>
 * Multinomial[] == 1
 * </pre>
 *
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Multinomial.html">Multinomial</a> */
public enum Multinomial {
  ;
  /** @param values
   * @return multinomial coefficient */
  public static Scalar of(int... values) {
    int sum = Arrays.stream(values).reduce(Math::addExact).orElse(0);
    Scalar scalar = Factorial.of(sum);
    for (int value : values)
      scalar = scalar.divide(Factorial.of(value));
    return scalar;
  }
}
