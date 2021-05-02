// code by jph
package ch.alpine.tensor.qty;

import java.util.Map;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** UnitSystem is an operator that maps a given {@link Quantity} to a {@link Quantity}
 * that makes use only of standard units defined by the unit system.
 * 
 * <p>Example when using the built-in SI definitions:
 * <pre>
 * UnitSystem.SI().apply(Quantity.of(1, "V")) == 1[A^-1*kg*m^2*s^-3]
 * UnitSystem.SI().apply(Quantity.of(125, "mi")) == 201168[m]
 * </pre>
 * 
 * <p>{@link SimpleUnitSystem} is a reference implementation of the interface
 * provided by the tensor library.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitSystem.html">UnitSystem</a> */
public interface UnitSystem extends ScalarUnaryOperator {
  /** @return international system of units, metric system with the base units:
   * "A", "cd", "K", "kg", "m", "mol", "s" */
  static UnitSystem SI() {
    return SiUnitSystem.INSTANCE.unitSystem;
  }

  /***************************************************/
  /** Example: for the SI unit system the map includes the entries
   * <pre>
   * "rad" -> 1
   * "Hz" -> 1[s^-1]
   * "W" -> 1[m^2*kg*s^-3]
   * "km" -> 1000[m]
   * ...
   * </pre>
   * 
   * @return unmodifiable view on map that defines conversions in the unit system, keys are atomic */
  Map<String, Scalar> map();

  /** Example: "kW*h" -> "kg*m^2*s^-2"
   * 
   * <p>inspired by
   * <a href="https://reference.wolfram.com/language/ref/UnitDimensions.html">UnitDimensions</a>
   * 
   * @param unit
   * @return base unit without multiplicative factor */
  Unit dimensions(Unit unit);
}
