// code by jph
package ch.alpine.tensor.num;

import java.util.NavigableMap;
import java.util.stream.IntStream;

import ch.alpine.tensor.ext.Integers;

/** unlike in Mathematica, the tensor library does not make restrictions on the parameter length
 * except length to be positive.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PermutationList.html">PermutationList</a> */
/* package */ enum PermutationList {
  ;
  /** Example:
   * <pre>
   * PermutationList[Cycles[{{2, 1}, {0, 5, 6}}], 9] == [5, 2, 1, 3, 4, 6, 0, 7, 8]
   * </pre>
   * 
   * @param cycles
   * @param length non-negative
   * @return */
  public static IntStream of(Cycles cycles, int length) {
    NavigableMap<Integer, Integer> navigableMap = cycles.navigableMap();
    return IntStream.range(0, Integers.requirePositiveOrZero(length)) //
        .map(i -> navigableMap.getOrDefault(i, i));
  }
}
