// code by jph
package ch.alpine.tensor.sca;

import java.math.MathContext;
import java.math.RoundingMode;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** provides the decimal representation of scalars that implement {@link NInterface}.
 * Supported types include {@link RationalScalar}, and {@link DecimalScalar}.
 * 
 * <p>In Mathematica, the N operator may be used with a parameter that specifies the precision.
 * If the parameter is omitted the result is in machine precision, i.e. 64-bit double.
 * <pre>
 * Sqrt[N[2]] == 1.4142135623730951`
 * Sqrt[N[2, 16]] == 1.41421356237309504880168872420969807857`16.30102999566398
 * </pre>
 * 
 * <p>The tensor library uses the following notation:
 * <pre>
 * Sqrt.of(N.DOUBLE.of(2)) == 1.4142135623730951
 * Sqrt.of(N.DECIMAL128.of(2)) == 1.414213562373095048801688724209698`34
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/N.html">N</a> */
public enum N {
  ;
  /** conversion to {@link DoubleScalar} */
  public static final ScalarUnaryOperator DOUBLE = new NDouble();
  /** conversion to {@link DecimalScalar} with higher than machine precision */
  public static final ScalarUnaryOperator DECIMAL128 = new NDecimal(MathContext.DECIMAL128);
  /** conversion to {@link DecimalScalar} equivalent to machine precision */
  public static final ScalarUnaryOperator DECIMAL64 = new NDecimal(MathContext.DECIMAL64);
  /** conversion to {@link DecimalScalar} with precision equivalent to 32-bit float */
  public static final ScalarUnaryOperator DECIMAL32 = new NDecimal(MathContext.DECIMAL32);

  /** creates an instance of N that supplies {@link DecimalScalar}s with precision
   * specified by mathContext.
   * 
   * @param precision is approximately the number of correct digits in the decimal encoding
   * @return conversion to given precision in context with RoundingMode.HALF_EVEN */
  public static ScalarUnaryOperator in(int precision) {
    return new NDecimal(new MathContext(precision, RoundingMode.HALF_EVEN));
  }
}
