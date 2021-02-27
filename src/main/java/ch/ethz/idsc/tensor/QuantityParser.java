// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

/* package */ enum QuantityParser {
  ;
  /** Example:
   * "9.81[m*s^-2]" -> Quantity.of(9.81, "m*s^-2")
   * 
   * @param string
   * @return
   * @throws Exception if given string cannot be parsed to a scalar of instance
   * {@link RealScalar}, {@link ComplexScalar}, or {@link Quantity} */
  public static Scalar of(String string) {
    int index = string.indexOf(Quantity.UNIT_OPENING_BRACKET);
    if (0 < index) {
      int last = string.indexOf(Quantity.UNIT_CLOSING_BRACKET);
      if (index < last && string.substring(last + 1).trim().isEmpty())
        return Quantity.of( //
            ScalarParser.of(string.substring(0, index)), //
            Unit.of(string.substring(index + 1, last)));
      throw new IllegalArgumentException(string);
    }
    return ScalarParser.of(string);
  }
}
