// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface Pivot {
  /** @param row
   * @param col fixed column
   * @param ind permutation
   * @param lhs matrix
   * @return index between row and ind.length - 1 that should be used as pivot element */
  int index(int row, int col, int[] ind, Tensor[] lhs);
}
