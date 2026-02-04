// code by jph
package ch.alpine.tensor.pdf;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomComplex.html">RandomComplex</a> */
public enum RandomComplex {
  ;
  public static Scalar of(RandomGenerator randomGenerator) {
    return ComplexScalar.of( //
        randomGenerator.nextDouble(), //
        randomGenerator.nextDouble());
  }

  public static Scalar of() {
    return of(ThreadLocalRandom.current());
  }
}
