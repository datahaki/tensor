// template from the Internet
// adapted by jph
package ch.alpine.tensor.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** CAREFUL: not suitable for strings of length 10k and above
 * 
 * Backtrace
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/EditDistance.html">EditDistance</a> */
public enum EditDistanceTraced {
  ;
  private record Stamp(int cost, Stamp prev, String info, char chr) {
  }

  /** @param origin
   * @param target
   * @return transition from origin to target */
  public static int of(String origin, String target) {
    final int m = origin.length();
    final int n = target.length();
    // Make a table to keep track of subproblem outcomes.
    Stamp[][] table = new Stamp[m + 1][n + 1];
    table[0][0] = new Stamp(0, null, "start", ' ');
    // Min number of operations = j: insert chars up to here
    for (int j = 1; j <= n; ++j)
      table[0][j] = new Stamp(1 + table[0][j - 1].cost(), table[0][j - 1], "insert", target.charAt(j - 1));
    // Min. operations = i: remove chars up to here
    for (int i = 1; i <= m; ++i)
      table[i][0] = new Stamp(1 + table[i - 1][0].cost(), table[i - 1][0], "remove", origin.charAt(i - 1));
    // ---
    // Fill in table[][] from the bottom up.
    for (int i = 1; i <= m; ++i)
      for (int j = 1; j <= n; ++j)
        // If the last characters are the same, ignore the last / char and continue with the rest of the string.
        table[i][j] = origin.charAt(i - 1) == target.charAt(j - 1) //
            ? new Stamp(table[i - 1][j - 1].cost(), table[i - 1][j - 1], "  keep", target.charAt(j - 1))
            : switch (ArgMin.of(Integers.asList(new int[] { //
                table[i][j - 1].cost(), // Insert
                table[i - 1][j].cost(), // Remove
                table[i - 1][j - 1].cost() // Replace
            }))) {
            case 0 -> new Stamp(1 + table[i][j - 1].cost(), table[i][j - 1], "insert", target.charAt(j - 1));
            case 1 -> new Stamp(1 + table[i - 1][j].cost(), table[i - 1][j], "remove", origin.charAt(i - 1));
            case 2 -> new Stamp(1 + table[i - 1][j - 1].cost(), table[i - 1][j - 1], "replce " + origin.charAt(i - 1), target.charAt(j - 1));
            default -> throw new IllegalStateException();
            };
    List<Stamp> list = new ArrayList<>(Math.max(m, n));
    {
      Stamp seed = table[m][n];
      do {
        list.add(seed);
        seed = seed.prev();
      } while (Objects.nonNull(seed.prev()));
    }
    Collections.reverse(list);
    // System.out.println("---");
    // System.out.println("[" + origin + "] -> [" + target + "]");
    // for (trace seed : list)
    // System.out.println(seed.cost() + " " + seed.info() + " " + seed.chr());
    // Lists.last(null);
    return list.get(list.size() - 1).cost();
  }

  /** @param origin
   * @return */
  public static Function<String, Integer> function(String origin) {
    Objects.requireNonNull(origin);
    return target -> of(origin, target);
  }
}
