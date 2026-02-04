// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.BinningMethods;
import ch.alpine.tensor.sca.Sign;

/** Reference:
 * "Radial Basis Functions in General Use", eq (3.7.6)
 * in NR, 2007
 * 
 * @see BinningMethods */
public class InverseMultiquadricVariogram extends MultiquadricVariogram {
  /** @param r0 non-negative */
  public static ScalarUnaryOperator of(Scalar r0) {
    return new InverseMultiquadricVariogram(Sign.requirePositiveOrZero(r0));
  }

  /** @param r0 non-negative */
  public static ScalarUnaryOperator of(Number r0) {
    return of(RealScalar.of(r0));
  }

  // ---
  private InverseMultiquadricVariogram(Scalar r0) {
    super(r0);
  }

  @Override // from TensorNorm
  public Scalar apply(Scalar r) {
    return super.apply(r).reciprocal();
  }
}
