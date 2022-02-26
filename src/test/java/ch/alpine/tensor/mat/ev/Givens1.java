// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sqrt;

record Givens1(int n, Scalar theta, int p, int q) {

  private static final Scalar SQRT2 = Sqrt.FUNCTION.apply(RealScalar.TWO);
  public Tensor matrix() {
    Chop.NONE.requireZero(Imag.FUNCTION.apply(theta));
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
