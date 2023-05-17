// code by jph
package ch.alpine.tensor.pdf;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomComplex.html">RandomComplex</a> */
public enum RandomComplex {
  ;
  private static final RandomGenerator RANDOM_GENERATOR = new SecureRandom();

  public static Scalar of(RandomGenerator randomGenerator) {
    return ComplexScalar.of( //
        randomGenerator.nextDouble(), //
        randomGenerator.nextDouble());
  }

  public static Scalar of() {
    return of(RANDOM_GENERATOR);
  }
}
