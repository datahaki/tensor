// code by jph
package ch.alpine.tensor.pdf.c;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.UnitConvert;

/* package */ enum StaticHelper {
  ;
  private static final ScalarUnaryOperator CONVERT_S = UnitConvert.SI().to("s");

  /** when using distributions with {@link DateTime} as one parameter
   * in many cases, the other parameter should be with unit "s"
   * (instead of "h") in order for all member functions provided by
   * the distribution to compute properly.
   * 
   * @param reference
   * @param scalar
   * @return */
  public static Scalar normal(Scalar reference, Scalar scalar) {
    return reference instanceof DateTime //
        ? CONVERT_S.apply(scalar)
        : scalar;
  }
}
