// code by jph
package ch.alpine.tensor.num;

import java.util.stream.IntStream;

import ch.alpine.tensor.ext.Integers;

/** implementation consistent with Mathematica, except that in the tensor library
 * the indices start at 0 instead of 1.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PermutationList.html">PermutationList</a> */
public enum PermutationList {
  ;
  /** Example:
   * <pre>
   * PermutationList[Cycles[{{2, 1}, {0, 5, 6}}], 9] == [5, 2, 1, 3, 4, 6, 0, 7, 8]
   * </pre>
   * 
   * @param cycles
   * @param length not less than cycles.minLength()
   * @return */
  public static int[] of(Cycles cycles, int length) {
    Integers.requirePositiveOrZero(Math.subtractExact(length, cycles.minLength()));
    return IntStream.range(0, length).map(cycles::replace).toArray();
  }
}
