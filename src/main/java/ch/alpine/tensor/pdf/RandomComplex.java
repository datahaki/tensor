// code by jph
package ch.alpine.tensor.pdf;

import java.security.SecureRandom;
import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomComplex.html">RandomComplex</a> */
public enum RandomComplex {
  ;
  private static final Random RANDOM = new SecureRandom();

  public static Scalar of(Random random) {
    return ComplexScalar.of( //
        random.nextDouble(), //
        random.nextDouble());
  }

  public static Scalar of() {
    return of(RANDOM);
  }
}
