// template from the Internet
// adapted by jph
package ch.alpine.tensor.ext;

import java.util.Objects;
import java.util.function.Function;

/** Remark: implementation determines distance only and thereby only uses O(min(n,m)) memory
 * where n and m are the lengths of the two given strings.
 * 
 * in the test scope there is code that tracks the best edit solution,
 * see EditDistanceTraced, that requires O(n*m) memory
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/EditDistance.html">EditDistance</a> */
public enum EditDistance {
  ;
  /** Remark: on a standard pc, for two strings of length 10k the computation of
   * distance takes about 2[s]
   * 
   * @param origin
   * @param target
   * @return transition from origin to target */
  public static int of(String origin, String target) {
    return origin.length() < target.length() //
        ? _of(target, origin)
        : _of(origin, target);
  }

  private static int _of(String origin, String target) {
    final int m = origin.length();
    final int n = target.length();
    Integers.requireLessEquals(n, m);
    // Make a table to keep track of subproblem outcomes.
    int[] prev = new int[n + 1];
    int[] next = new int[n + 1];
    prev[0] = 0;
    // Min number of operations = j: insert chars up to here
    for (int j = 1; j <= n; ++j)
      prev[j] = 1 + prev[j - 1];
    // ---
    for (int i = 1; i <= m; ++i) {
      // Min. operations = i: remove chars up to here
      next[0] = i;
      for (int j = 1; j <= n; ++j)
        // If the last characters are the same, ignore the last / char and continue with the rest of the string.
        if (origin.charAt(i - 1) == target.charAt(j - 1))
          next[j] = prev[j - 1];
        else {
          int[] data = new int[] { //
              next[j - 1], // Insert
              prev[j], // Remove
              prev[j - 1] }; // Replace
          next[j] = data[ArgMin.of(Integers.asList(data))] + 1;
        }
      int[] temp = prev;
      prev = next;
      next = temp;
    }
    return prev[n];
  }

  /** @param origin
   * @return */
  public static Function<String, Integer> function(String origin) {
    Objects.requireNonNull(origin);
    return target -> of(origin, target);
  }
}
