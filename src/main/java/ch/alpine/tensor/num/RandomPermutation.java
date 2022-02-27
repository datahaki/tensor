// code by jph
package ch.alpine.tensor.num;

import java.security.SecureRandom;
import java.util.Random;

import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomPermutation.html">RandomPermutation</a> */
public enum RandomPermutation {
  ;
  private static final Random RANDOM = new SecureRandom();

  /** @param n non-negative
   * @param random
   * @return
   * @see Integers#isPermutation(int[])
   * @throws Exception if n is negative */
  public static int[] of(int n, Random random) {
    return Ordering.INCREASING.of(RandomVariate.of(UniformDistribution.unit(), random, n));
  }

  /** Example:
   * <pre>
   * RandomPermutation[10] == {7, 6, 4, 5, 3, 2, 0, 1, 9, 8}
   * </pre>
   * 
   * @param n non-negative
   * @return array of length n
   * @throws Exception if n is negative */
  public static int[] of(int n) {
    return of(n, RANDOM);
  }

  /** @param n non-negative
   * @param random
   * @return
   * @throws Exception if n is negative */
  public static Cycles cycles(int n, Random random) {
    return PermutationCycles.unsafe(of(n, random));
  }

  /** Example:
   * <pre>
   * RandomPermutation[12] == {{0, 2, 9, 1}, {3, 10, 5, 4}, {6, 7}}
   * </pre>
   * 
   * @param n non-negative
   * @return random cycles from symmetric group S_n
   * @throws Exception if n is negative */
  public static Cycles cycles(int n) {
    return cycles(n, RANDOM);
  }
}
