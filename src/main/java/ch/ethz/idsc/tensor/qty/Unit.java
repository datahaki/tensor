// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** class is intended for testing and demonstration */
public interface Unit extends Serializable {
  static final char OPENING_BRACKET = '[';
  static final char CLOSING_BRACKET = ']';

  /** @param string, for instance "[m*s^-2]"
   * @return */
  static Unit of(String string) {
    if (OPENING_BRACKET != string.charAt(0))
      throw new RuntimeException(string);
    if (CLOSING_BRACKET != string.charAt(string.length() - 1))
      throw new RuntimeException(string);
    string = string.substring(1, string.length() - 1); // remove brackets
    String[] split = string.split("\\*");
    Map<String, Scalar> map = new HashMap<>();
    for (int count = 0; count < split.length; ++count) {
      final String token = split[count];
      int index = token.indexOf('^');
      final String unit;
      final Scalar exponent;
      if (0 <= index) {
        unit = token.substring(0, index);
        exponent = Scalars.fromString(token.substring(index + 1));
      } else {
        unit = token;
        exponent = RealScalar.ONE;
      }
      map.put(unit.trim(), exponent);
    }
    return new UnitImpl(map);
  }

  /** @return true if unit is unit-less */
  boolean isEmpty();

  /** [kg*m^2] -> [kg^-1*m^-2]
   * 
   * function negate is equivalent to {@link #multiply(Scalar)} with factor -1
   * 
   * @return */
  Unit negate();

  /** [m] + [s^2] -> [m*s^2]
   * 
   * @param unit
   * @return */
  Unit add(Unit unit);

  /** [m^2] * 3 -> [m^6]
   * 
   * @param factor
   * @return */
  Unit multiply(Scalar factor);

  /** @return map with elemental units and exponents */
  Map<String, Scalar> map();
}
