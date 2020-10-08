// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.NavigableMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.ext.Cache;

/** associates strings with instances of unit */
/* package */ enum UnitMemo {
  ;
  /* package for testing */ static final int MAX_SIZE = 768;
  /* package for testing */ static final Function<String, Unit> CACHE = Cache.of(UnitMemo::create, MAX_SIZE);

  public static Unit of(String string) {
    return CACHE.apply(string);
  }

  /** @param string, for instance "A*kg^-1*s^2"
   * @return unit
   * @throws Exception if string is not a valid expression for a unit */
  private static Unit create(String string) {
    NavigableMap<String, Scalar> map = new TreeMap<>();
    StringTokenizer stringTokenizer = new StringTokenizer(string, Unit.JOIN_DELIMITER);
    while (stringTokenizer.hasMoreTokens()) {
      String token = stringTokenizer.nextToken();
      int index = token.indexOf(Unit.POWER_DELIMITER);
      final String unit;
      final Scalar exponent;
      if (0 <= index) {
        unit = token.substring(0, index).trim();
        exponent = Scalars.fromString(token.substring(index + 1));
      } else {
        unit = token.trim();
        if (unit.isEmpty())
          continue;
        exponent = RealScalar.ONE;
      }
      String key = StaticHelper.requireAtomic(unit);
      if (map.containsKey(key)) { // exponent exists
        Scalar sum = map.get(key).add(exponent);
        if (Scalars.isZero(sum))
          map.remove(key); // exponents cancel
        else
          map.put(key, sum); // update total exponent
      } else //
      if (Scalars.nonZero(exponent)) // introduce exponent
        map.put(key, exponent);
    }
    return new UnitImpl(map);
  }
}
