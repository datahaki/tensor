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
/* package */ enum UnitParser {
  ;
  private static final int MAX_SIZE = 768;
  private static final Function<String, Unit> CACHE = Cache.of(UnitParser::create, MAX_SIZE);

  /** @param string, for instance "A*kg^-1*s^2"
   * @return unit
   * @throws Exception if string is not a valid expression for a unit */
  public static Unit of(String string) {
    return CACHE.apply(string);
  }

  private static Unit create(String string) {
    NavigableMap<String, Scalar> map = new TreeMap<>();
    StringTokenizer stringTokenizer = new StringTokenizer(string, Unit.JOIN_DELIMITER);
    while (stringTokenizer.hasMoreTokens()) {
      String token = stringTokenizer.nextToken();
      int index = token.indexOf(Unit.POWER_DELIMITER);
      if (0 <= index) {
        String key = StaticHelper.requireAtomic(token.substring(0, index).trim());
        Scalar exponent = Scalars.fromString(token.substring(index + 1));
        if (Scalars.nonZero(exponent))
          StaticHelper.merge(map, key, exponent);
      } else {
        String unit = token.trim();
        if (!unit.isEmpty())
          StaticHelper.merge(map, StaticHelper.requireAtomic(unit), RealScalar.ONE);
      }
    }
    return new UnitImpl(map);
  }
}
