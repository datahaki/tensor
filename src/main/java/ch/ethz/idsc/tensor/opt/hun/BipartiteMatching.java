// code by jph
package ch.ethz.idsc.tensor.opt.hun;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface BipartiteMatching {
  /** value of element in array returned by matching */
  static final int UNASSIGNED = -1;

  /** @param matrix
   * @return */
  static BipartiteMatching of(Tensor matrix) {
    return new HungarianAlgorithm(matrix);
  }

  /** @return array of length equal to the rows of matrix */
  int[] matching();

  /** @return total cost of minimal solution */
  Scalar minimum();
}
