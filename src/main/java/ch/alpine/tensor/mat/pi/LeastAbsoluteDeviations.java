// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.sca.SoftThreshold;

/** Reference:
 * "Distributed Optimization and Statistical Learning via the
 * Alternating Direction Method of Multipliers"
 * 6.1 Least Absolute Deviations
 * by Stephen Boyd, Neal Parikh, Eric Chu, Borja Peleato, and Jonathan Eckstein, 2011 */
/* package */ enum LeastAbsoluteDeviations {
  ;
  /** @param A
   * @param b
   * @param rho
   * @return x with small Vector1Norm[A.x - b] */
  public static Tensor of(Tensor A, Tensor b, Scalar rho) {
    TensorUnaryOperator solver = LeastSquares.operator(SingularValueDecomposition.of(A));
    // initialize
    Tensor u = b.map(Scalar::zero);
    Tensor z = b.map(Scalar::zero);
    ScalarUnaryOperator suo = SoftThreshold.of(rho);
    Tensor x = null;
    for (int i = 0; i < 100; ++i) {
      x = solver.apply(b.add(z).subtract(u));
      // ---
      z = A.dot(x).subtract(b).add(u).map(suo);
      u = A.dot(x).add(u).subtract(z).subtract(b);
    }
    return x;
  }
}
