// code by jph
package ch.alpine.tensor.opt.ts;

import java.util.Arrays;
import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.num.RandomPermutation;

/** Reference:
 * "Algorithmik" by Uwe Schoening, p.328 */
public class Tsp2OptHeuristic {
  private final Tensor matrix;
  private final int n;
  private final Random random;
  private final int[] index;

  public Tsp2OptHeuristic(Tensor matrix, Random random) {
    this.matrix = SymmetricMatrixQ.require(matrix);
    n = matrix.length();
    this.random = random;
    index = RandomPermutation.of(matrix.length(), random);
  }

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
          // Arrays.fill(array, -1); // for check only
          int c1 = -1;
          for (int c0 = 0; c0 <= i; ++c0)
            array[++c1] = index[c0];
          for (int c0 = j; i < c0; --c0)
            array[++c1] = index[c0];
          for (int c0 = j + 1; c0 < n; c0++)
            array[++c1] = index[c0];
          System.arraycopy(array, 0, index, 0, n);
          // Integers.requirePermutation(index);
          return true;
        }
        break;
      }
    }
    return false;
  }

  public Scalar cost() {
    Scalar cost = matrix.Get(index[n - 1], index[0]);
    for (int c0 = 1; c0 < n; ++c0)
      cost = cost.add(matrix.Get(index[c0 - 1], index[c0]));
    return cost;
  }

  public int[] index() {
    return Arrays.copyOf(index, index.length);
  }
}
