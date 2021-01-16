// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;

public enum UnitSystems {
  ;
  /** Examples:
   * A unit system with "min" as the default time unit:
   * <pre>
   * UnitSystems.rotate[UnitSystem.SI(), "s", "min"]
   * </pre>
   * 
   * A unit system with "Hz" as the default time unit:
   * <pre>
   * UnitSystems.rotate[UnitSystem.SI(), "s", "Hz"]
   * </pre>
   * 
   * A unit system with Newton "N" instead of "s":
   * <pre>
   * UnitSystems.rotate[UnitSystem.SI(), "s", "N"]
   * </pre>
   * 
   * @param unitSystem
   * @param prev
   * @param next
   * @return */
  public static UnitSystem rotate(UnitSystem unitSystem, String prev, String next) {
    Scalar value = StaticHelper.conversion(unitSystem, prev, next);
    if (prev.equals(next))
      return unitSystem;
    Map<String, Scalar> map = new HashMap<>(unitSystem.map()); // copy map
    map.remove(next);
    map.put(prev, value);
    return focus(SimpleUnitSystem._from(map));
  }

  private static UnitSystem focus(UnitSystem unitSystem) {
    return SimpleUnitSystem.from(unitSystem.map().entrySet().stream() //
        .collect(Collectors.toMap(Entry::getKey, entry -> unitSystem.apply(entry.getValue())))); // strict
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
