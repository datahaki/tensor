// code by jph
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.ReIm;

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
    ReIm reIm = ReIm.of(z);
    return I_NEGATE.multiply(Erf.FUNCTION.apply(ComplexScalar.of(reIm.im().negate(), reIm.re())));
  }
}
