// code by Samuel J. Stauber
// adapted by jph
package ch.alpine.tensor.opt.hun;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.red.Min;

/* package */ class HungarianAlgorithmTree {
  private final Bipartition bipartition;
  private final Set<Integer> S = new HashSet<>();
  private final Set<Integer> nlsMinusT = new HashSet<>();
  private final int[] escapeFromY;
  private final int[] escapeFromX;
  private final Scalar[] xLabel;
  private final Scalar[] yLabel;
  private final Scalar[][] matrix;
  private final int[] yMatch;
  private final Scalar[] alpha;

  public HungarianAlgorithmTree(Scalar[] xLabel, int[] yMatch, Scalar[][] matrix) {
    int dim = xLabel.length;
    this.xLabel = xLabel;
    this.yLabel = Stream.of(xLabel).map(Scalar::zero).toArray(Scalar[]::new);
    this.yMatch = yMatch;
    this.matrix = matrix;
    bipartition = BipartitionImpl.empty(dim);
    escapeFromY = new int[dim];
    escapeFromX = new int[dim];
    alpha = Stream.of(xLabel).map(Scalar::zero).toArray(Scalar[]::new);
    resetEscape();
  }

  public void setAlpha(int x) {
    Arrays.fill(escapeFromY, x);
    Scalar xLabelX = xLabel[x];
    for (int y = 0; y < alpha.length; ++y)
      alpha[y] = matrix[x][y].subtract(yLabel[y]).subtract(xLabelX);
    S.add(x);
  }

  public void updateAlpha(int x) { // slows down (n x n)-Problem
    for (int y : bipartition.notNodes()) {
      Scalar cmp = matrix[x][y].subtract(yLabel[y]).subtract(xLabel[x]);
      if (Scalars.lessThan(cmp, alpha[y])) {
        alpha[y] = cmp;
        escapeFromY[y] = x;
      }
      if (Scalars.isZero(cmp))
        nlsMinusT.add(y);
    }
  }

  private int pickNlsMinusT() {
    if (nlsMinusT.isEmpty()) {
      bipartition.notNodes().stream().filter(y -> Scalars.isZero(alpha[y])).forEach(nlsMinusT::add);
      Scalar min = bipartition.notNodes().stream().map(y -> alpha[y]).reduce(Min::of).orElseThrow();
      if (Scalars.nonZero(min)) // use of eps
        updateLabels(min);
    }
    for (int y : nlsMinusT)
      if (yMatch[y] == BipartiteMatching.UNASSIGNED) {
        nlsMinusT.remove(y);
        return y;
      }
    return nlsMinusT.stream() //
        .min((i, j) -> Scalars.compare(alpha[i], alpha[j])).orElseThrow();
  }

  private void updateLabels(Scalar delta) { // slows down (n x n)-Problem
    S.stream().forEach(x -> xLabel[x] = xLabel[x].add(delta));
    bipartition.nodesStream().forEach(y -> yLabel[y] = yLabel[y].subtract(delta));
    for (int y : bipartition.notNodes()) {
      alpha[y] = alpha[y].subtract(delta);
      if (Scalars.isZero(alpha[y]))
        nlsMinusT.add(y);
    }
  }

  public int escapeFromX(int x) {
    return escapeFromX[x];
  }

  public int escapeFromY(int y) {
    return escapeFromY[y];
  }

  public int addS() {
    while (true) {
      int y = pickNlsMinusT();
      if (yMatch[y] == BipartiteMatching.UNASSIGNED)
        return y;
      int x = addT(y);
      S.add(x);
      updateAlpha(x);
    }
  }

  private int addT(int y) {
    escapeFromX[yMatch[y]] = y;
    bipartition.add(y);
    nlsMinusT.remove(y);
    return yMatch[y];
  }

  public void clear() {
    resetEscape();
    S.clear();
    bipartition.clear();
    nlsMinusT.clear();
  }

  private void resetEscape() {
    Arrays.fill(escapeFromX, BipartiteMatching.UNASSIGNED);
    Arrays.fill(escapeFromY, BipartiteMatching.UNASSIGNED);
  }
}
