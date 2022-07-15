// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface Pivot {
  /** @param row
   * @param col fixed column
   * @param ind permutation
   * @param lhs matrix
   * @return row index between c0 and ind.length that should be used as pivot element
   * if all pivot candidates are 0, the function returns c0 */
  int get(int row, int col, int[] ind, Tensor[] lhs);
}
