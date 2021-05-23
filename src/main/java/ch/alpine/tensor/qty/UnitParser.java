// code by jph
package ch.alpine.tensor.qty;

import java.util.Map;
import java.util.NavigableMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** associates strings with instances of unit */
/* package */ enum UnitParser {
  ;
  /** @param string, for instance "A*kg^-1*s^2"
   * @return unit
   * @throws Exception if string is not a valid expression for a unit */
  public static Unit of(String string) {
    NavigableMap<String, Scalar> navigableMap = new TreeMap<>();
    StringTokenizer stringTokenizer = new StringTokenizer(string, Unit.JOIN_DELIMITER);
    while (stringTokenizer.hasMoreTokens()) {
      String token = stringTokenizer.nextToken();
      int index = token.indexOf(Unit.POWER_DELIMITER);
      if (0 <= index) {
        String key = requireAtomic(token.substring(0, index).strip());
        Scalar exponent = Scalars.fromString(token.substring(index + 1));
        if (Scalars.nonZero(exponent))
          merge(navigableMap, key, exponent);
      } else {
        String key = token.strip();
        if (!key.isEmpty())
          merge(navigableMap, requireAtomic(key), RealScalar.ONE);
      }
    }
    return UnitImpl.create(navigableMap);
  }

  /** atomic unit may consist of roman letters in lower case a-z,
   * upper case A-Z, as well as the underscore character '_', and
   * the percent character `%` */
  private static final Predicate<String> PREDICATE = //
      Pattern.compile("[%A-Z_a-z]+").asMatchPredicate();

  /** @param key atomic unit expression, for instance "kg"
   * @return given key
   * @throws Exception if given key is not an atomic unit expression */
  public static String requireAtomic(String key) {
    if (PREDICATE.test(key))
      return key;
    throw new IllegalArgumentException(key);
  }

  /** @param map
   * @param key satisfies {@link requireAtomic}
   * @param exponent non-zero */
  public static void merge(Map<String, Scalar> map, String key, Scalar exponent) {
    if (map.containsKey(key)) {
      Scalar sum = map.get(key).add(exponent);
      if (Scalars.isZero(sum))
        map.remove(key); // exponents cancel out
      else
        map.put(key, sum); // exponent is updated
    } else
      map.put(key, exponent); // unit is introduced
  }
}
