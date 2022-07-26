// code by jph
package ch.alpine.tensor.pdf;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

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
  /** The default constructor of {@link Random} determines the seed at time of creation
   * using {@link System#nanoTime()}. Typically, the implementation will produce a different
   * random sequence for two successive program executions. */
  private static final Random RANDOM = new SecureRandom();

  /** @param distribution
   * @param random
   * @return random variate from given distribution */
  public static Scalar of(Distribution distribution, Random random) {
    return _of((RandomVariateInterface) distribution, random); // terminal
  }

  /** @param distribution
   * @return random variate from given distribution */
  public static Scalar of(Distribution distribution) {
    return of(distribution, RANDOM); // of # interface, random
  }

  /** @param distribution
   * @param random
   * @param dimensions
   * @return array of random variates from given interface with given dimensions */
  public static Tensor of(Distribution distribution, Random random, List<Integer> dimensions) {
    RandomVariateInterface randomVariateInterface = (RandomVariateInterface) distribution;
    return Array.fill(() -> _of(randomVariateInterface, random), dimensions); // terminal
  }

  /** @param distribution
   * @param dimensions
   * @return array of random variates with given dimensions */
  public static Tensor of(Distribution distribution, List<Integer> dimensions) {
    return of(distribution, RANDOM, dimensions); // of # interface, random, list
  }

  /** @param distribution
   * @param random
   * @param dimensions
   * @return array of random variates from given interface with given dimensions */
  public static Tensor of(Distribution distribution, Random random, int... dimensions) {
    return of(distribution, random, Integers.asList(dimensions)); // of # interface, random, list
  }

  /** @param distribution
   * @param dimensions
   * @return array of random variates with given dimensions */
  public static Tensor of(Distribution distribution, int... dimensions) {
    return of(distribution, Integers.asList(dimensions)); // of # interface, list
  }

  // helper function
  private static Scalar _of(RandomVariateInterface randomVariateInterface, Random random) {
    return randomVariateInterface.randomVariate(random);
  }
}
