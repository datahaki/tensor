// code by jph
package ch.alpine.tensor.qty;

import java.util.Map;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** An instance of {@link Unit} is immutable.
 * 
 * Two instances of {@link Unit} are equal if their map()s are equal. */
public interface Unit {
  /** Example: cd*m*s */
  static final String JOIN_DELIMITER = "*";
  /** Example: A*kg^-2 */
  static final char POWER_DELIMITER = '^';
  /** holds the dimension-less unit ONE
   * Mathematica: "DimensionlessUnit" */
  static final Unit ONE = of("");

  /** The precedence for parsing a string expression of unit is first '*', then '^'.
   * That means, the brackets are obsolete "N*K^(1/2)" == "N*K^1/2".
   * The exponent behind '^' is parsed using {@link Scalars#fromString(String)}.
   * 
   * @param string, for instance "m*s^-2", or "m^6*bar*mol^-2*K^1/2"
   * @return */
  static Unit of(String string) {
    return UnitParser.of(string);
  }

  // ---
  /** function negate is equivalent to {@link #multiply(Scalar)} with factor -1
   * 
   * Example: in order to compute the reciprocal of a quantity, the exponents
   * of the elemental units are negated. 1 / (X[kg*m^2]) is accompanied by the
   * calculation [kg*m^2] -> [kg^-1*m^-2]
   * 
   * @return */
  Unit negate();

  /** "addition" of units is performed in order to compute a product of quantities.
   * For example, X[m*s] * Y[s^2] requires to collect all elemental units and add
   * their exponents: [m*s] + [s^2] -> [m*s^3]
   * 
   * If the resulting exponent equals 0, the elemental unit is removed altogether.
   * 
   * @param unit
   * @return */
  Unit add(Unit unit);

  /** Hint: used in power, and sqrt
   * 
   * [kg*m^2] * 3 -> [kg^3*m^6]
   * 
   * @param scalar
   * @return
   * @throws Exception if scalar is not instance of RealScalar */
  Unit multiply(Scalar scalar);

  /** Example: Unit.of("kg^2*m^-1*s") returns an unmodifiable map with the entry set
   * {"kg" -> 2, "m" -> -1, "s" -> 1}
   * 
   * @return unmodifiable map with elemental units as keys and their exponents as values */
  Map<String, Scalar> map();
}
