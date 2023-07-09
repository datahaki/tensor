// template from the Internet
// adapted by jph
package ch.alpine.tensor.ext;

import java.util.Objects;
import java.util.function.Function;

/** Remark: implementation determines distance only
 * in the test scope there is code that tracks the best edit solution,
 * see EditDistanceTraced
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/EditDistance.html">EditDistance</a> */
public enum EditDistance {
  ;
  /** @param origin
   * @param target
   * @return transition from origin to target */
  public static int of(String origin, String target) {
    final int m = origin.length();
    final int n = target.length();
    // Make a table to keep track of subproblem outcomes.
    int[][] table = new int[m + 1][n + 1];
    table[0][0] = 0;
    // Min number of operations = j: insert chars up to here
    for (int j = 1; j <= n; ++j)
      table[0][j] = 1 + table[0][j - 1];
    // Min. operations = i: remove chars up to here
    for (int i = 1; i <= m; ++i)
      table[i][0] = 1 + table[i - 1][0];
    // ---
    // Fill in table[][] from the bottom up.
    for (int i = 1; i <= m; ++i)
      for (int j = 1; j <= n; ++j)
        // If the last characters are the same, ignore the last / char and continue with the rest of the string.
        table[i][j] = origin.charAt(i - 1) == target.charAt(j - 1) //
            ? table[i - 1][j - 1]
            : switch (ArgMin.of(Integers.asList(new int[] { //
                table[i][j - 1], // Insert
                table[i - 1][j], // Remove
                table[i - 1][j - 1] // Replace
            }))) {
            case 0 -> 1 + table[i][j - 1];
            case 1 -> 1 + table[i - 1][j];
            case 2 -> 1 + table[i - 1][j - 1];
            default -> throw new IllegalStateException();
            };
    return table[m][n];
  }

  /** @param origin
   * @return */
  public static Function<String, Integer> function(String origin) {
    Objects.requireNonNull(origin);
    return target -> of(origin, target);
  }
}
