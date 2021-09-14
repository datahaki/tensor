// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomPermutation.html">RandomPermutation</a> */
public enum RandomPermutation {
  ;
  /** Example:
   * RandomPermutation.ofLength(10)) == {7, 6, 4, 5, 3, 2, 0, 1, 9, 8}
   * 
   * @param n non-negative
   * @return array of length n
   * @throws Exception if n is negative */
  public static int[] ofLength(int n) {
    return Ordering.INCREASING.of(RandomVariate.of(UniformDistribution.unit(), n));
  }
}
