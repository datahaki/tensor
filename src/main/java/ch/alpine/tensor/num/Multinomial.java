// code by jph
package ch.alpine.tensor.num;

import java.util.Arrays;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.ArgMax;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.gam.Factorial;

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
    if (1 < values.length) {
      int index = ArgMax.of(Integers.asList(values));
      int total = Arrays.stream(values).reduce(Math::addExact).orElse(0);
      Scalar scalar = IntStream.rangeClosed(values[index] + 1, total) //
          .mapToObj(RealScalar::of) //
          .reduce(Scalar::multiply) //
          .orElse(RealScalar.ONE);
      int count = 0;
      for (int value : values)
        if (count++ != index)
          scalar = scalar.divide(Factorial.of(value));
      return scalar;
    }
    return RealScalar.ONE;
  }
}
