// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.red.Max;

/** Mathematica uses the definition
 * <pre>
 * Ramp[x] == x * UnitStep[x]
 * </pre>
 * 
 * The tensor library simply uses
 * <pre>
 * Ramp[x] == Max[x.zero(), x]
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Ramp.html">Ramp</a> */
public enum Ramp implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return Max.of(scalar.zero(), scalar);
  }
}
