// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.pow.Sqrt;

class Givens1 {
  private final int n;
  private final Scalar theta;
  private final int p;
  private final int q;

  Givens1(int n, Scalar theta, int p, int q) {
    this.n = n;
    this.theta = theta;
    this.p = p;
    this.q = q;
  }

  private static final Scalar SQRT2 = Sqrt.FUNCTION.apply(RealScalar.TWO);

  public Tensor matrix() {
    Chop.NONE.requireZero(Im.FUNCTION.apply(theta));
    Tensor tensor = IdentityMatrix.of(n);
    {
      Scalar t_n = ComplexScalar.unit(theta.negate()); // Exp[I*theta]
      tensor.set(t_n.divide(SQRT2), p, p);
      tensor.set(t_n.divide(SQRT2), q, p);
    }
    {
      Scalar t_p = ComplexScalar.unit(theta); // exp[I*theta]
      tensor.set(t_p.divide(SQRT2).negate(), p, q);
      tensor.set(t_p.divide(SQRT2), q, q);
    }
    return tensor;
  }
}
