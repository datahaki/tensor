// code by jph
package ch.alpine.tensor.usr;

import java.util.Properties;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;

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
        Scalar scalar = quantity.value();
        Unit unit = quantity.unit();
        String unitString = unit.map().entrySet() //
            .stream() //
            .map(entry -> {
              String string = entry.getValue().toString();
              String reduce = string.equals("1") ? "" : '^' + string;
              return '\"' + UNIT_NAMES.getProperty(entry.getKey()) + '\"' + reduce;
            }) //
            .collect(Collectors.joining("*"));
        return String.format("Quantity[%s, %s]", scalar, unitString);
      }
      return tensor.toString();
    }
    return tensor.stream().map(MathematicaForm::of).collect(EMBRACE);
  }
}
