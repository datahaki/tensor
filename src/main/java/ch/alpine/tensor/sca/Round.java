// code by jph
package ch.alpine.tensor.sca;

import java.math.BigDecimal;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.RoundingInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;

/** consistent with Mathematica:
 * <pre>
 * Round[+11.5] == +12
 * Round[-11.5] == -12
 * </pre>
 * 
 * not consistent with java.lang.Math::round which rounds -11.5 to -11.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Round.html">Round</a>
 * 
 * @see RoundingInterface
 * @see Ceiling
 * @see Floor */
public enum Round implements ScalarUnaryOperator {
  FUNCTION;

  public static final ScalarUnaryOperator _1 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.1")));
  public static final ScalarUnaryOperator _2 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.01")));
  public static final ScalarUnaryOperator _3 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.001")));
  public static final ScalarUnaryOperator _4 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.0001")));
  public static final ScalarUnaryOperator _5 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.00001")));
  public static final ScalarUnaryOperator _6 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.000001")));
  public static final ScalarUnaryOperator _7 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.0000001")));
  public static final ScalarUnaryOperator _8 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.00000001")));
  public static final ScalarUnaryOperator _9 = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.000000001")));

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RoundingInterface) {
      RoundingInterface roundingInterface = (RoundingInterface) scalar;
      return roundingInterface.round();
    }
    throw TensorRuntimeException.of(scalar);
  }

  /** Examples:
   * <pre>
   * Round[+4.0] == +4
   * Round[+4.2] == +4
   * Round[+4.9] == +5
   * Round[-7.2] == -7
   * </pre>
   * 
   * @param scalar
   * @return
   * @throws Exception if output is outside encodable range
   * {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}
   * @throws Exception if input scalar is instance of {@link Quantity} */
  public static int intValueExact(Scalar scalar) {
    return Scalars.intValueExact(FUNCTION.apply(scalar));
  }

  /** see documentation {@link #intValueExact(Scalar)}
   * 
   * @param scalar
   * @return
   * @throws Exception if output is outside encodable range
   * {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}
   * @throws Exception if input scalar is instance of {@link Quantity} */
  public static long longValueExact(Scalar scalar) {
    return Scalars.longValueExact(FUNCTION.apply(scalar));
  }

  /** rounds all entries of tensor to nearest integers, with
   * ties rounding to positive infinity.
   * 
   * @param tensor
   * @return tensor with all entries replaced by their rounded values */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }

  /** for best results, the parameter increment should be a instance of
   * {@link DecimalScalar}, or {@link RationalScalar}
   * Examples:
   * DecimalScalar.of(0.1), or RationalScalar.of(1, 10)
   * 
   * <p>if instead increment is a {@link DoubleScalar} the return value
   * may suffer from numeric round off error in the style of "3.4000000000000004"
   * 
   * @param increment non-zero
   * @return */
  public static ScalarUnaryOperator toMultipleOf(Scalar increment) {
    return scalar -> FUNCTION.apply(scalar.divide(increment)).multiply(increment);
  }
}
