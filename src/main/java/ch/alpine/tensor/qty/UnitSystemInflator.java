// code by jph
package ch.alpine.tensor.qty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.PackageTestAccess;

/* package */ class UnitSystemInflator {
  public static final char INFLATOR = '_';

  /** @param properties
   * @return */
  public static UnitSystem of(Properties properties) {
    return SimpleUnitSystem.from(new UnitSystemInflator(StaticHelper.stringScalarMap(properties)).map);
  }

  // ---
  private final Map<String, Scalar> map = new HashMap<>();
  private final Set<String> skipped = new HashSet<>();
  private final Set<String> atoms;

  @PackageTestAccess
  UnitSystemInflator(Map<String, Scalar> input) {
    atoms = input.values().stream() //
        .map(QuantityUnit::of) //
        .map(Unit::map) //
        .map(Map::keySet) //
        .flatMap(Set::stream) //
        .collect(Collectors.toSet());
    // give precedence to explicitly listed units: ft, pt, PS
    for (Entry<String, Scalar> entry : input.entrySet()) {
      String key = entry.getKey();
      if (key.charAt(0) != INFLATOR)
        map.put(key, entry.getValue()); // key is unit
    }
    // handle specifications that have underscore prefix, e.g. "_g"
    for (Entry<String, Scalar> entry : input.entrySet()) {
      String key = entry.getKey();
      if (key.charAt(0) == INFLATOR) { //
        String suffix = key.substring(1); // "g"
        for (MetricPrefix metricPrefix : MetricPrefix.values()) {
          String unit = metricPrefix.prefix(suffix); // "mg", or "kg"
          if (!atoms.contains(unit)) // exclude "kg"
            if (map.containsKey(unit)) // collisions are "PS", "pt", "ft"
              skipped.add(unit);
            else
              map.put(unit, entry.getValue().multiply(metricPrefix.factor()));
        }
      }
    }
  }

  /** Example:
   * in the unit system system defined by the tensor library
   * the set of atoms consists of [cd, A, B, s, K, mol, kg, m]
   * 
   * @return */
  public Set<String> atoms() {
    return atoms;
  }

  /** Example:
   * in the unit system system defined by the tensor library
   * the collisions are "PS", "pt", or "ft"
   * 
   * @return */
  public Set<String> skipped() {
    return skipped;
  }
}
