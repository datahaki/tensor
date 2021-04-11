// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ class UnitFactor {
  private final NavigableMap<String, Scalar> navigableMap = new TreeMap<>();
  private final Unit unit;
  private Scalar product = null; // avoids to introduce a multiplicative 1

  public UnitFactor(Map<String, Scalar> map, Unit unit) {
    this.unit = unit;
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
  }

  public Scalar getScalar(Quantity quantity) {
    return Objects.isNull(product) //
        ? quantity
        : StaticHelper.multiply(product.multiply(quantity.value()), UnitImpl.create(navigableMap));
  }

  public Unit getUnit() {
    return Objects.isNull(product) //
        ? unit
        : QuantityUnit.of(StaticHelper.multiply(product, UnitImpl.create(navigableMap)));
  }
}