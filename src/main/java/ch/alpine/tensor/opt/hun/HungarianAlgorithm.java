// code by Samuel J. Stauber
// adapted by jph
package ch.alpine.tensor.opt.hun;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Min;

/** This program executes the Hungarian Algorithm in order to solve a bipartite
 * matching problem. An entry [i][j] of the Scalar[n][m]-input-array represents
 * the cost of matching worker i to job j. An entry [i] of the int[n]-output-
 * array stores the best job j that was assigned to worker i. If there is no
 * job for a worker, i.e. j>i, the entry in the output-array will read -1.
 * 
 * The resulting matching will have minimum cost and therefore is an optimum.
 * All entries in the output array are unique.
 * 
 * https://en.wikipedia.org/wiki/Hungarian_algorithm */
/* package */ class HungarianAlgorithm extends HungarianAlgorithmGraph {
  private transient final HungarianAlgorithmTree hungarianAlgorithmTree;
  private transient final Set<Integer> freeX = new HashSet<>();
  private int iterations = 0;

  public HungarianAlgorithm(Tensor _matrix) {
    super(_matrix);
    Scalar[] xLabel = Arrays.stream(matrix) //
        .map(vector -> Arrays.stream(vector).reduce(Min::of).orElseThrow()) //
        .toArray(Scalar[]::new);
    hungarianAlgorithmTree = new HungarianAlgorithmTree(xLabel, yMatch, matrix);
    setInitialMatching(xLabel);
    initializeFreeNodes();
    while (!isSolved()) {
      int x = freeX.stream().findFirst().orElseThrow();
      int y = hungarianAlgorithmTree.setAlphaAndS(x);
      augmentMatching(x, y);
      hungarianAlgorithmTree.clear();
      ++iterations;
    }
  }

  private void setInitialMatching(Scalar[] xLabel) {
    // TODO TENSOR ALG calling match should probably be implemented differently
    for (int x = 0; x < xMatch.length; ++x) {
      Scalar xValue = xLabel[x];
      for (int y = 0; y < yMatch.length; ++y)
        if (yMatch[y] == UNASSIGNED && //
            matrix[x][y].equals(xValue)) {
          match(x, y);
          break;
        }
    }
  }

  private void initializeFreeNodes() {
    IntStream.range(0, xMatch.length).filter(i -> xMatch[i] == UNASSIGNED).forEach(freeX::add);
  }

  private void augmentMatching(int stoppingX, int startingY) {
    int x;
    int y = startingY;
    do {
      x = hungarianAlgorithmTree.escapeFromY(y);
      match(x, y);
      y = hungarianAlgorithmTree.escapeFromX(x);
    } while (x != stoppingX);
    freeX.remove(stoppingX);
  }

  private boolean isSolved() {
    return freeX.isEmpty();
  }

  public int iterations() {
    return iterations;
  }
}
