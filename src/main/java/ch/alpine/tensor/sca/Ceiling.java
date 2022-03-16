// code by jph
package ch.alpine.tensor.sca;

import java.math.BigDecimal;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.RoundingInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;

/** Examples:
 * <pre>
 * Ceiling[+3.9] == +4
 * Ceiling[-8.2] == -8
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Ceiling.html">Ceiling</a>
 * 
 * @see RoundingInterface
 * @see Floor
 * @see Round */
public enum Ceiling implements ScalarUnaryOperator {
  FUNCTION;

  public static final ScalarUnaryOperator _1 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.1")));
  public static final ScalarUnaryOperator _2 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.01")));
  public static final ScalarUnaryOperator _3 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.001")));
  public static final ScalarUnaryOperator _4 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.0001")));
  public static final ScalarUnaryOperator _5 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.00001")));
  public static final ScalarUnaryOperator _6 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.000001")));
  public static final ScalarUnaryOperator _7 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.0000001")));
  public static final ScalarUnaryOperator _8 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.00000001")));
  public static final ScalarUnaryOperator _9 = Ceiling.toMultipleOf(DecimalScalar.of(new BigDecimal("0.000000001")));

  /** @param scalar instance if {@link RealScalar}
   * @return best integer scalar approximation to ceiling of scalar
   * @throws TensorRuntimeException if scalar is Infinity, or NaN */
  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RoundingInterface roundingInterface)
      return roundingInterface.ceiling();
    throw TensorRuntimeException.of(scalar);
  }

  /** Examples:
   * <pre>
   * Ceiling[+4.0] == +4
   * Ceiling[+4.2] == +5
   * Ceiling[+4.9] == +5
   * Ceiling[-7.2] == -7
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
   * @return */
  public static long longValueExact(Scalar scalar) {
    return Scalars.longValueExact(FUNCTION.apply(scalar));
  }

  /** @param tensor
   * @return tensor with all entries replaced by their ceiling */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }

  /** @param increment non-zero
   * @return */
  public static ScalarUnaryOperator toMultipleOf(Scalar increment) {
    return scalar -> FUNCTION.apply(scalar.divide(increment)).multiply(increment);
  }
}
