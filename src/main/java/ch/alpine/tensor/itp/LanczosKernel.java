// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sinc;

/** The Lanczos kernel is the function t -> sinc[t] Sinc[t/semi]
 * 
 * The implementation only evaluates the kernel in [-semi, semi] */
/* package */ class LanczosKernel implements ScalarUnaryOperator {
  public static final LanczosKernel _3 = new LanczosKernel(3);
  // ---
  private final int semi;
  private final Scalar bound;

  /** @param semi positive */
  public LanczosKernel(int semi) {
    this.semi = semi;
    bound = Sign.requirePositive(RealScalar.of(semi));
  }

  @Override
  public Scalar apply(Scalar scalar) { // scalar is in the interval [-semi, semi]
    Scalar _scalar = scalar.multiply(Pi.VALUE);
    return Sinc.FUNCTION.apply(_scalar).multiply(Sinc.FUNCTION.apply(_scalar.divide(bound)));
  }

  public int semi() {
    return semi;
  }
}
