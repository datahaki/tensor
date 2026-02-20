// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.qty.Quantity;

/** Examples:
 * <pre>
 * UnitStep[-3] == 0
 * UnitStep[Quantity.of(-2.7, "s^-1")] == 0
 * UnitStep[0] == 1
 * UnitStep[10] == 1
 * </pre>
 * 
 * <p>implementation is <em>not</em> consistent with Mathematica for input of type
 * {@link Quantity}:
 * Mathematica::UnitStep[Quantity[1, "Meters"]] == UnitStep[Quantity[1, "Meters"]]
 * 
 * UnitStep can be considered the derivative of {@link Ramp}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitStep.html">UnitStep</a> */
public enum UnitStep implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return Boole.of(Sign.isPositiveOrZero(scalar));
  }
}
