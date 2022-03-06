// code by jph
package ch.alpine.tensor.num;

import java.util.Random;

import ch.alpine.tensor.Scalar;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomPrime.html">RandomPrime</a> */
public enum RandomPrime {
  ;
  public static Scalar of(int n, Random random) {
    if (Prime.MAX_INDEX < n)
      throw new IllegalArgumentException(Integer.toString(n));
    return Prime.of(random.nextInt(n) + 1);
  }
}
