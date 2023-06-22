// code by gjoel
// formula from https://en.wikipedia.org/wiki/Error_function
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <pre>
 * Erf[z] == -Erf[-z]
 * Erf[z] == 1 - Erfc[z]
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Erf.html">Erf</a> */
public enum Erf implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar z) {
    return RealScalar.ONE.subtract(Erfc.FUNCTION.apply(z));
  }
}
