// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.N;

/* package */ enum CsvHelper implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar EMPTY = StringScalar.of("\"\"");

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof StringScalar)
      return wrap(scalar);
    if (scalar instanceof RationalScalar)
      return IntegerQ.of(scalar) //
          ? scalar
          : N.DOUBLE.apply(scalar);
    if (scalar instanceof DecimalScalar)
      return N.DOUBLE.apply(scalar);
    if (scalar instanceof ComplexScalar)
      throw TensorRuntimeException.of(scalar);
    if (scalar instanceof Quantity)
      throw TensorRuntimeException.of(scalar);
    return scalar;
  }

  public static Scalar wrap(Scalar scalar) {
    String string = scalar.toString();
    if (string.isEmpty())
      return EMPTY;
    int e = string.length() - 1;
    if (string.charAt(0) == '\"' && string.charAt(e) == '\"') {
      requireQuotesFree(string.substring(1, e));
      return scalar;
    }
    return StringScalar.of("\"" + requireQuotesFree(string) + "\"");
  }

  public static String requireQuotesFree(String string) {
    int index = string.indexOf('\"');
    if (0 <= index)
      throw new IllegalArgumentException(string);
    return string;
  }
}
