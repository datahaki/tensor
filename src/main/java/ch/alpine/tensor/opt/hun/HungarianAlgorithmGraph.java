// code by Samuel J. Stauber
// adapted by jph
package ch.alpine.tensor.opt.hun;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.red.EqualsReduce;

/* package */ abstract class HungarianAlgorithmGraph implements BipartiteMatching, Serializable {
  private final int rows;
  private final int cols;
  protected final Scalar[][] matrix;
  protected final int[] xMatch;
  protected final int[] yMatch;
  protected int matchCount = 0;

  /** @param _matrix with entries of unique unit */
  protected HungarianAlgorithmGraph(Tensor _matrix) {
    rows = Integers.requirePositive(_matrix.length());
    cols = Unprotect.dimension1(_matrix);
    int dim = Math.max(rows, cols);
    Tensor normal = PadRight.with(EqualsReduce.zero(_matrix), dim, dim).apply(rows <= cols //
        ? _matrix
        : Transpose.of(_matrix));
    matrix = ScalarArray.ofMatrix(normal);
    xMatch = new int[dim];
    Arrays.fill(xMatch, UNASSIGNED);
    yMatch = new int[dim];
    Arrays.fill(yMatch, UNASSIGNED);
  }

  protected final void match(int x, int y) {
    xMatch[x] = y;
    yMatch[y] = x;
    ++matchCount;
  }

  @Override // from BipartiteMatching
  public final int[] matching() {
    int[] result = new int[rows];
    int[] resvec = rows <= cols ? xMatch : yMatch;
    for (int x = 0; x < rows; ++x) {
      int y = resvec[x];
      result[x] = cols <= y ? UNASSIGNED : y;
    }
    return result;
  }

  @Override // from BipartiteMatching
  public final Scalar minimum() {
    Stream<Scalar> stream = rows <= cols //
        ? IntStream.range(0, rows).mapToObj(i -> matrix[i][xMatch[i]]) //
        : IntStream.range(0, rows).filter(i -> yMatch[i] < cols).mapToObj(i -> matrix[yMatch[i]][i]);
    return stream.reduce(Scalar::add).orElseThrow();
  }
}
