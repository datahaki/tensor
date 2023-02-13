// code by jph
package ch.alpine.tensor.opt.ts;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.num.RandomPermutation;

/** greedy heuristic to optimize traveling salesman problem
 * 
 * Careful: implementation is not thread safe, i.e. do not call methods
 * simultaneously from different threads.
 * 
 * Reference:
 * "Algorithmik" by Uwe Schoening, p.328 */
public class Tsp2OptHeuristic {
  private final Tensor matrix;
  private final int n;
  private final RandomGenerator random;
  private final int[] index;

  /** the entries in the symmetric distance matrix may be negative
   * 
   * @param matrix symmetric
   * @param random */
  public Tsp2OptHeuristic(Tensor matrix, RandomGenerator random) {
    this.matrix = SymmetricMatrixQ.require(matrix);
    n = matrix.length();
    this.random = random;
    index = RandomPermutation.of(matrix.length(), random);
  }

  /** one random re-routing attempt
   * 
   * @return true if previous solution could be improved, resulting in a modified
   * {@link #index} and lower {@link #cost()} */
  public boolean next() {
    if (n < 4)
      return false;
    while (true) {
      int i = random.nextInt(n);
      int j = random.nextInt(n);
      int mod = Math.floorMod(i - j, n);
      if (1 < mod && mod != n - 1) {
        if (j < i) {
          int t = i;
          i = j;
          j = t;
        }
        int ip = Math.floorMod(i + 1, n);
        int jp = Math.floorMod(j + 1, n);
        Scalar di_ip = matrix.Get(index[i], index[ip]);
        Scalar dj_jp = matrix.Get(index[j], index[jp]);
        Scalar di_j_ = matrix.Get(index[i], index[j]);
        Scalar dipjp = matrix.Get(index[ip], index[jp]);
        if (Scalars.lessThan(di_j_.add(dipjp), di_ip.add(dj_jp))) {
          int[] array = new int[n];
          int c1 = -1;
          for (int c0 = 0; c0 <= i; ++c0)
            array[++c1] = index[c0];
          for (int c0 = j; i < c0; --c0)
            array[++c1] = index[c0];
          for (int c0 = j + 1; c0 < n; c0++)
            array[++c1] = index[c0];
          System.arraycopy(array, 0, index, 0, n);
          return true;
        }
        break;
      }
    }
    return false;
  }

  /** @return cost of current solution */
  public Scalar cost() {
    int last = n - 1;
    Scalar cost = matrix.Get(index[last], index[0]);
    for (int i = 0; i < last;)
      cost = cost.add(matrix.Get(index[i], index[++i]));
    return cost;
  }

  /** @return current solution */
  public int[] index() {
    return Arrays.copyOf(index, index.length);
  }
}
