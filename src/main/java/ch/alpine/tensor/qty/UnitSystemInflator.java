// code by jph
package ch.alpine.tensor.qty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

// TODO clean up
/* package */ class UnitSystemInflator {
  public static final char INFLATOR = '_';
  private final Map<String, Scalar> map = new HashMap<>();
  private final Set<String> atoms;
  private final Set<String> skipped = new HashSet<>();

  public UnitSystemInflator(Properties properties) {
    Map<String, Scalar> input = properties.stringPropertyNames().stream().collect(Collectors.toMap( //
        Function.identity(), //
        key -> Scalars.fromString(properties.getProperty(key))));
    atoms = input.values().stream() //
        .map(QuantityUnit::of) //
        .map(Unit::map) //
        .map(Map::keySet) //
        .flatMap(Set::stream) //
        .collect(Collectors.toSet());
    for (Entry<String, Scalar> entry : input.entrySet()) {
      String key = entry.getKey();
      if (key.charAt(0) != INFLATOR) {
        if (map.containsKey(key))
          throw new IllegalArgumentException(key);
        map.put(key, entry.getValue());
      }
    }
    for (Entry<String, Scalar> entry : input.entrySet()) {
      String key = entry.getKey();
      if (key.charAt(0) == INFLATOR) {
        String suffix = key.substring(1);
        for (MetricPrefix metricPrefix : MetricPrefix.values()) {
          String result = metricPrefix.prefix(suffix);
          if (!atoms.contains(result))
            putDefensive(result, entry.getValue().multiply(metricPrefix.factor()));
        }
      }
    }
    for (Scalar scalar : new LinkedList<>(map.values())) {
      for (String atom : QuantityUnit.of(scalar).map().keySet()) // example: m, kg, s, A
        if (map.containsKey(atom)) {
          Scalar conversion = map.get(atom);
          if (conversion.equals(Quantity.of(1, atom))) {
            System.err.println("SHOULD NOT BE NECESSARY");
            map.remove(atom);
          }
          System.out.println("atom self " + atom + " " + conversion);
        }
    }
  }

  /** @param key
   * @param value */
  private void putDefensive(String key, Scalar value) {
    if (map.containsKey(key))
      skipped.add(key);
    else
      map.put(key, value);
  }

  public Map<String, Scalar> getMap() {
    return map;
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
