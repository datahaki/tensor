// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.QRDecomposition;

/** Example: in some cases, it is known a priori whether the data passed to
 * an algorithm produces symmetric, or rank deficient linear systems. Then,
 * an implementation of {@link LinearSolver} can be passed together with
 * the data. */
// LONGTERM EXPERIMENTAL
public enum LinearSolvers implements LinearSolver {
  /** matrix square with full rank */
  GAUSSIAN {
    @Override
    public Tensor solve(Tensor matrix, Tensor b) {
      return GaussianElimination.of(matrix, b, Pivots.ARGMAX_ABS);
    }
  },
  /** matrix square with full rank, and symmetric */
  CHOLESKY {
    @Override
    public Tensor solve(Tensor matrix, Tensor b) {
      return CholeskyDecomposition.of(matrix).solve(b);
    }
  },
  /** matrix with maximal rank */
  QR {
    @Override
    public Tensor solve(Tensor matrix, Tensor b) {
      return QRDecomposition.of(matrix).solve(b);
    }
  },
  /** matrix possibly rank deficient */
  SVD {
    @Override
    public Tensor solve(Tensor matrix, Tensor b) {
      return LeastSquares.usingSvd(matrix, b);
    }
  };
}
