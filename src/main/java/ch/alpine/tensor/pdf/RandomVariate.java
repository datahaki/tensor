// code by jph
package ch.alpine.tensor.pdf;

import java.security.SecureRandom;
import java.util.List;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Integers;

/** RandomVariate generates single random variates, or arrays of random variates
 * from a given {@link Distribution}, or {@link RandomVariateInterface}.
 * 
 * Example:
 * <pre>
 * Distribution distribution = NormalDistribution.standard();
 * Tensor matrix = RandomVariate.of(distribution, 5, 3);
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomVariate.html">RandomVariate</a> */
public enum RandomVariate {
  ;
  /** Typically, the implementation will produce a different
   * random sequence for two successive program executions. */
  private static final RandomGenerator RANDOM_GENERATOR = new SecureRandom();

  /** @param distribution
   * @param randomGenerator
   * @return random variate from given distribution */
  public static Scalar of(Distribution distribution, RandomGenerator randomGenerator) {
    return _of((RandomVariateInterface) distribution, randomGenerator); // terminal
  }

  /** @param distribution
   * @return random variate from given distribution */
  public static Scalar of(Distribution distribution) {
    return of(distribution, RANDOM_GENERATOR); // of # interface, random
  }

  /** @param distribution
   * @param randomGenerator
   * @param dimensions
   * @return array of random variates from given interface with given dimensions */
  public static Tensor of(Distribution distribution, RandomGenerator randomGenerator, List<Integer> dimensions) {
    RandomVariateInterface randomVariateInterface = (RandomVariateInterface) distribution;
    return Array.fill(() -> _of(randomVariateInterface, randomGenerator), dimensions); // terminal
  }

  /** @param distribution
   * @param dimensions
   * @return array of random variates with given dimensions */
  public static Tensor of(Distribution distribution, List<Integer> dimensions) {
    return of(distribution, RANDOM_GENERATOR, dimensions); // of # interface, random, list
  }

  /** @param distribution
   * @param randomGenerator
   * @param dimensions
   * @return array of random variates from given interface with given dimensions */
  public static Tensor of(Distribution distribution, RandomGenerator randomGenerator, int... dimensions) {
    return of(distribution, randomGenerator, Integers.asList(dimensions)); // of # interface, random, list
  }

  /** @param distribution
   * @param dimensions
   * @return array of random variates with given dimensions */
  public static Tensor of(Distribution distribution, int... dimensions) {
    return of(distribution, Integers.asList(dimensions)); // of # interface, list
  }

  // helper function
  private static Scalar _of(RandomVariateInterface randomVariateInterface, RandomGenerator randomGenerator) {
    return randomVariateInterface.randomVariate(randomGenerator);
  }
}
