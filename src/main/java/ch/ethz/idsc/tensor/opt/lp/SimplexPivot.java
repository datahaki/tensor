// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface SimplexPivot {
  /** @param tab simplex tableau with n + 1 columns
   * @param j < n
   * @param n
   * @return */
  int get(Tensor tab, int j, int n);
}
