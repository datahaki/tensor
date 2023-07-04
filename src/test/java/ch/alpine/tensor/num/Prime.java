// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.Import;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Prime.html">Prime</a> */
public enum Prime {
  ;
  private static final Tensor LIST = Import.of("/ch/alpine/tensor/num/primes.vector");
  public static final int MAX_INDEX = LIST.length();

  /** @param n strictly positive
   * @return n-th prime
   * @throws Exception if n is not positive, or n-th prime is cannot be provided by implementation */
  public static Scalar of(int n) {
    return LIST.Get(n - 1);
  }
}
