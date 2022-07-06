// code by jph
package ch.alpine.tensor.qty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;

/** utilities for {@link UnitSystem}s */
public enum UnitSystems {
  ;
  /** Example: the base units of the SI unit system are
   * "A", "cd", "K", "kg", "m", "mol", "s"
   * 
   * @param unitSystem
   * @return base units of the given unitSystem */
  public static Set<String> base(UnitSystem unitSystem) {
    return StaticHelper.base(unitSystem.map().values());
  }

  // ---
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
   * @param prev a base unit of the given unitSystem
   * @param next not a base unit of the given unitSystem, unless next equals prev
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

  // ---
  /** @param u1
   * @param u2
   * @return unit system with conversions from both u1 and u2
   * @throws Exception if u1 and u2 contain a key that maps to a different value */
  public static UnitSystem join(UnitSystem u1, UnitSystem u2) {
    Map<String, Scalar> map = new HashMap<>(u1.map());
    for (Entry<String, Scalar> entry : u2.map().entrySet()) {
      String key = entry.getKey();
      Scalar value = entry.getValue();
      if (map.containsKey(key)) {
        if (!map.get(key).equals(value))
          throw Throw.of(map.get(key), value);
      } else
        map.put(key, value);
    }
    return SimpleUnitSystem.from(map);
  }
}
