// code by jph
package ch.alpine.tensor.num;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomPermutation.html">RandomPermutation</a> */
public enum RandomPermutation {
  ;
  /** @param n non-negative
   * @param randomGenerator
   * @return
   * @see Integers#isPermutation(int[])
   * @throws Exception if n is negative */
  public static int[] of(int n, RandomGenerator randomGenerator) {
    return Ordering.INCREASING.of(RandomVariate.of(UniformDistribution.unit(), randomGenerator, n));
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
    return of(n, ThreadLocalRandom.current());
  }

  /** @param n non-negative
   * @param randomGenerator
   * @return
   * @throws Exception if n is negative */
  public static Cycles cycles(int n, RandomGenerator randomGenerator) {
    return PermutationCycles.unsafe(of(n, randomGenerator));
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
    return cycles(n, ThreadLocalRandom.current());
  }
}
