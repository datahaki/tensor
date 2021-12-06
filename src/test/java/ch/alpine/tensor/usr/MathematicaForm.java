// code by jph
package ch.alpine.tensor.usr;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.qty.Quantity;

/** function exists to export expressions from the tensor library to a string
 * that can be parsed by Mathematica */
public enum MathematicaForm {
  ;
  private static final String OPENING_BRACKET_STRING = Character.toString(Tensor.OPENING_BRACKET);
  private static final String CLOSING_BRACKET_STRING = Character.toString(Tensor.CLOSING_BRACKET);
  private static final Collector<CharSequence, ?, String> EMBRACE = //
      Collectors.joining(", ", OPENING_BRACKET_STRING, CLOSING_BRACKET_STRING);
  private static final Properties UNIT_NAMES = ResourceData.properties("/unit/names.properties");

  public static String of(Tensor tensor) {
    if (tensor instanceof Scalar) {
      if (tensor instanceof Quantity) {
        Quantity quantity = (Quantity) tensor;
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
    return '\"' + UNIT_NAMES.getProperty(entry.getKey()) + '\"' + reduce;
  }
}
