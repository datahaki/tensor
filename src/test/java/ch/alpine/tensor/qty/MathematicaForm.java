// code by jph
package ch.alpine.tensor.qty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ResourceData;

/** function exists to export expressions from the tensor library to a string
 * that can be parsed by Mathematica */
public enum MathematicaForm {
  INSTANCE;

  private static final String OPENING_BRACKET_STRING = Character.toString(Tensor.OPENING_BRACKET);
  private static final String CLOSING_BRACKET_STRING = Character.toString(Tensor.CLOSING_BRACKET);
  private static final Collector<CharSequence, ?, String> EMBRACE = //
      Collectors.joining(", ", OPENING_BRACKET_STRING, CLOSING_BRACKET_STRING);
  private final Map<String, String> map = new HashMap<>();

  MathematicaForm() {
    Set<String> block = ResourceData.properties("/ch/alpine/tensor/qty/si.properties").stringPropertyNames();
    Properties properties = ResourceData.properties("/ch/alpine/tensor/qty/names.properties");
    for (String key : properties.stringPropertyNames()) {
      if (key.charAt(0) == UnitSystemInflator.INFLATOR) {
        String value = properties.getProperty(key);
        key = key.substring(1);
        for (MetricPrefix metricPrefix : MetricPrefix.values()) {
          String result = metricPrefix.prefix(key);
          if (!block.contains(result))
            set(result, metricPrefix.english(value));
        }
      } else
        map.put(key, properties.getProperty(key));
    }
  }

  private void set(String key, String value) {
    if (map.containsKey(key))
      throw new IllegalArgumentException(key);
    map.put(key, value);
  }

  public Map<String, String> getMap() {
    return Collections.unmodifiableMap(map);
  }

  public static String of(Tensor tensor) {
    if (tensor instanceof Scalar) {
      if (tensor instanceof Quantity quantity) {
        String unit = quantity.unit().map().entrySet() //
            .stream() //
            .map(MathematicaForm::of) //
            .collect(Collectors.joining("*"));
        return String.format("Quantity[%s, %s]", quantity.value(), unit);
      }
      return tensor.toString();
    }
    return tensor.stream().map(MathematicaForm::of).collect(EMBRACE);
  }

  private static String of(Entry<String, Scalar> entry) {
    String string = entry.getValue().toString();
    String reduce = string.equals("1") ? "" : '^' + string;
    return '\"' + INSTANCE.map.get(entry.getKey()) + '\"' + reduce;
  }
}
