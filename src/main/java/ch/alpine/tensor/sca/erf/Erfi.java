// code by jph
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

/** <pre>
 * Erfi[z] == -I * Erf[I z]
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Erfi.html">Erfi</a> */
public enum Erfi implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar I_NEGATE = ComplexScalar.of(0.0, -1.0);

  @Override
  public Scalar apply(Scalar z) {
    Scalar re = Re.FUNCTION.apply(z);
    Scalar im = Im.FUNCTION.apply(z);
    return I_NEGATE.multiply(Erf.FUNCTION.apply(ComplexScalar.of(im.negate(), re)));
  }
}
