// code by jph
package ch.alpine.tensor.num;

import java.util.NavigableMap;
import java.util.TreeMap;

import ch.alpine.tensor.ext.Integers;

/** implementation consistent with Mathematica, except that in the tensor library
 * the indices start at 0 instead of 1.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PermutationCycles.html">PermutationCycles</a> */
public enum PermutationCycles {
  ;
  /** @param sigma permutation
   * @return
   * @throws Exception if given sigma is not a permutation */
  public static Cycles of(int... sigma) {
    return unsafe(Integers.requirePermutation(sigma));
  }

  /* package */ static Cycles unsafe(int... sigma) {
    NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
    for (int index = 0; index < sigma.length; ++index)
      if (index != sigma[index])
        navigableMap.put(index, sigma[index]);
    return new Cycles(navigableMap);
  }
}
