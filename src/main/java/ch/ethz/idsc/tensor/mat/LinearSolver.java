// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;

// LONGTERM EXPERIMENTAL
@FunctionalInterface
public interface LinearSolver {
  /** @param matrix
   * @param b
   * @return x with matrix . x approximately b */
  Tensor solve(Tensor matrix, Tensor b);
}
