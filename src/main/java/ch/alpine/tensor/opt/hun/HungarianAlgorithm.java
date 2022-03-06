// code by Samuel J. Stauber
// adapted by jph
package ch.alpine.tensor.opt.hun;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
  private transient final Set<Integer> freeY = new HashSet<>();
  private int iterations = 0;

  public HungarianAlgorithm(Tensor _matrix) {
    super(_matrix);
    Scalar[] xLabel = Stream.of(matrix) //
        .map(vector -> Stream.of(vector).reduce(Min::of).orElseThrow()) //
        .toArray(Scalar[]::new);
    hungarianAlgorithmTree = new HungarianAlgorithmTree(xLabel, yMatch, matrix);
    setInitialMatching(xLabel);
    initializeFreeNodes();
    while (!isSolved()) {
      int x = pickFreeX();
      int y = hungarianAlgorithmTree.addS();
      augmentMatching(x, y);
      hungarianAlgorithmTree.clear();
      ++iterations;
    }
  }

  private void setInitialMatching(Scalar[] xLabel) {
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
    IntStream.range(0, yMatch.length).filter(i -> yMatch[i] == UNASSIGNED).forEach(freeY::add);
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
    freeY.remove(startingY);
  }

  private boolean isSolved() {
    return freeX.isEmpty();
  }

  private int pickFreeX() {
    int x = freeX.stream().findFirst().orElseThrow();
    hungarianAlgorithmTree.setAlpha(x);
    return x;
  }

  public int iterations() {
    return iterations;
  }
}
