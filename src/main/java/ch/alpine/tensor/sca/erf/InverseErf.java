// code by jph
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <pre>
 * InverseErf[p] == InverseErfc[1 - p]
 * </pre>
 * 
 * <p>Reference:
 * "Incomplete Gamma Function" in NR, 2007
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/InverseErf.html">InverseErf</a> */
public enum InverseErf implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return InverseErfc.FUNCTION.apply(RealScalar.ONE.subtract(scalar));
  }
}
