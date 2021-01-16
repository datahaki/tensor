// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public enum UnitSystems {
  ;
  /** Example:
   * UnitSystems.rotate[UnitSystem.SI(), "s", "min"]
   * makes
   * 
   * @param unitSystem
   * @param string
   * @return */
  public static UnitSystem rotate(UnitSystem unitSystem, String prev, String next) {
    Unit unit_prev = Unit.of(prev);
    Unit unit_next = Unit.of(next);
    KnownUnitQ.in(unitSystem).require(unit_prev);
    KnownUnitQ.in(unitSystem).require(unit_next);
    if (prev.equals(next))
      return unitSystem;
    Scalar factor = Quantity.of(RealScalar.ONE, unit_next);
    Scalar value = unitSystem.apply(Quantity.of(RealScalar.ONE, unit_prev).divide(factor)).multiply(factor);
    return focus(invalid(unitSystem, prev, next, value));
  }

  private static UnitSystem invalid(UnitSystem unitSystem, String prev, String next, Scalar value) {
    Map<String, Scalar> map = new HashMap<>(unitSystem.map()); // copy map
    map.remove(next);
    map.put(prev, value);
    return SimpleUnitSystem._from(map); // non-strict
  }

  private static UnitSystem focus(UnitSystem unitSystem) {
    Map<String, Scalar> map = new HashMap<>();
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet()) {
      Scalar prev = unitSystem.apply(entry.getValue());
      if (!unitSystem.apply(prev).equals(prev)) // invariance
        throw new IllegalArgumentException();
      map.put(entry.getKey(), prev);
    }
    return SimpleUnitSystem.from(map); // strict
  }

  /***************************************************/
  public static UnitSystem join(UnitSystem u1, UnitSystem u2) {
    Map<String, Scalar> map = new HashMap<>(u1.map());
    u2.map().entrySet().stream() //
        .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
    if (map.size() != u1.map().size() + u2.map().size())
      throw new IllegalArgumentException();
    return SimpleUnitSystem.from(map);
  }
}
