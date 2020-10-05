// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** associates strings with instances of unit */
/* package */ enum UnitMemo {
  INSTANCE;

  /* package for testing */ static final int MAX_SIZE = 768;
  private final Map<String, Unit> map = new LinkedHashMap<String, Unit>(MAX_SIZE * 4 / 3, 0.75f, true) {
    private static final long serialVersionUID = 1215071882604314833L;

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Unit> eldest) {
      return MAX_SIZE < size();
    }
  };

  /** @param string, for instance "A*kg^-1*s^2"
   * @return unit
   * @throws Exception if string is not a valid expression for a unit */
  public Unit lookup(String string) {
    Unit unit = map.get(string);
    if (Objects.isNull(unit)) {
      unit = create(string);
      synchronized (map) {
        map.put(string, unit);
      }
    }
    return unit;
  }

  // helper function
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

  /* package for testing */ int map_size() {
    return map.size();
  }
}
