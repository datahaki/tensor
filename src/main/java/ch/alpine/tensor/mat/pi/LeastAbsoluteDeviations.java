// code by jph
package ch.alpine.tensor.mat.pi;

import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.SoftThreshold;

/** Reference:
 * "Distributed Optimization and Statistical Learning via the
 * Alternating Direction Method of Multipliers"
 * 6.1 Least Absolute Deviations
 * by Stephen Boyd, Neal Parikh, Eric Chu, Borja Peleato, and Jonathan Eckstein, 2011 */
/* package */ class LeastAbsoluteDeviations {
  /** @param A
   * @param b
   * @param rho
   * @return x with small Vector1Norm[A.x - b] */
  public static Tensor of(Tensor A, Tensor b, Scalar rho) {
    Random random = new Random(2);
    Tensor pinv = PseudoInverse.of(A);
    Tensor u = b.map(Scalar::zero);
    Tensor z = b.map(Scalar::zero);
    Tensor x = pinv.dot(b);
    Tensor ax_b = A.dot(x).subtract(b);
    Scalar min = Vector1Norm.of(ax_b);
    Tensor x_bst = x;
    // QUEST TENSOR MAT not final implementation
    for (int i = 0; i < 100; ++i) {
      x = pinv.dot(b.add(z).subtract(u));
      ax_b = A.dot(x).subtract(b);
      Scalar cmp = Vector1Norm.of(ax_b);
      if (Scalars.lessThan(cmp, min)) {
        min = cmp;
        x_bst = x;
      }
      rho = RealScalar.of(random.nextDouble() * 2);
      ScalarUnaryOperator suo = SoftThreshold.of(Clips.absolute(rho));
      z = ax_b.add(u).map(suo);
      u = u.add(ax_b).subtract(z);
    }
    return x_bst;
  }
}
