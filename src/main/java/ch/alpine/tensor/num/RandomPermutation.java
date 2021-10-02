// code by jph
package ch.alpine.tensor.num;

import java.security.SecureRandom;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomPermutation.html">RandomPermutation</a> */
public enum RandomPermutation {
  ;
  private static final Random RANDOM = new SecureRandom();

  /** Example:
   * RandomPermutation[12] == {{0, 2, 9, 1}, {3, 10, 5, 4}, {6, 7}}
   * 
   * @param n non-negative
   * @return random cycles from symmetric group S_n */
  public static Cycles of(int n) {
    return of(n, RANDOM);
  }

  /** @param n non-negative
   * @param random
   * @return */
  public static Cycles of(int n, Random random) {
    NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
    int[] perm = ofLength(n, random);
    for (int index = 0; index < n; ++index)
      if (index != perm[index])
        navigableMap.put(index, perm[index]);
    return new Cycles(navigableMap);
  }

  /** Example:
   * RandomPermutation.ofLength(10)) == {7, 6, 4, 5, 3, 2, 0, 1, 9, 8}
   * 
   * @param n non-negative
   * @return array of length n
   * @throws Exception if n is negative */
  // TODO function name bad
  public static int[] ofLength(int n) {
    return ofLength(n, RANDOM);
  }

  /** @param n non-negative
   * @param random
   * @return
   * @throws Exception if n is negative */
  // TODO function name bad
  public static int[] ofLength(int n, Random random) {
    return Ordering.INCREASING.of(RandomVariate.of(UniformDistribution.unit(), random, n));
  }
}
