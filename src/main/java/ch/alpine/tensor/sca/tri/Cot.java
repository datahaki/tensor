// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <pre>
 * Cot[NaN] == NaN
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Cot.html">Cot</a> */
public enum Cot implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return Tan.FUNCTION.apply(scalar).reciprocal();
  }
}
