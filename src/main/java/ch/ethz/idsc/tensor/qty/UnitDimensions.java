// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.sca.Power;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitDimensions.html">UnitDimensions</a> */
public class UnitDimensions implements Serializable {
  private static final int MAX_SIZE = 768;
  @SuppressWarnings("unchecked")
  private final Cache<Unit, Unit> cache = //
      Cache.of((Function<Unit, Unit> & Serializable) this::base, MAX_SIZE);
  private final Map<String, Scalar> map;

  public UnitDimensions(UnitSystem unitSystem) {
    map = unitSystem.map();
  }

  /** Example: "kW*h" -> "kg*m^2*s^-2"
   * 
   * @param unit
   * @return */
  public Unit toBase(Unit unit) {
    return cache.apply(unit);
  }

  private Unit base(Unit unit) {
    NavigableMap<String, Scalar> navigableMap = new TreeMap<>();
    Scalar product = null; // avoids to introduce a multiplicative 1
    for (Entry<String, Scalar> entry : unit.map().entrySet()) {
      Scalar lookup = map.get(entry.getKey());
      if (Objects.isNull(lookup)) // in case of base unit, e.g. "m" for SI
        navigableMap.put(entry.getKey(), entry.getValue());
      else { // in case of unit definitions, e.g. "Pa" for SI
        navigableMap.remove(entry.getKey());
        Scalar factor = Power.of(lookup, entry.getValue());
        product = Objects.isNull(product) //
            ? factor
            : product.multiply(factor);
      }
    }
    return Objects.isNull(product) //
        ? unit
        : QuantityUnit.of(StaticHelper.multiply(product, UnitImpl.create(navigableMap)));
  }
}
