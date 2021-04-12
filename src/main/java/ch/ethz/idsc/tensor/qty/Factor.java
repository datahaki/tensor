// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ interface Factor {
  static Factor of(Map<String, Scalar> map, Unit unit) {
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
        ? FactorEmpty.INSTANCE
        : new FactorProduct(StaticHelper.multiply(product, UnitImpl.create(navigableMap)));
  }

  /** @param quantity
   * @return */
  Scalar times(Quantity quantity);

  /** @param unit
   * @return */
  Unit getUnit(Unit unit);
}
