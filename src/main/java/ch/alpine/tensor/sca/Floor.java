// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.RoundingInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;

/** Examples:
 * <pre>
 * Floor[+3.9] == +3
 * Floor[-8.2] == -9
 * </pre>
 * 
 * <pre>
 * Floor[NaN] == NaN
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Floor.html">Floor</a>
 * 
 * @see RoundingInterface
 * @see Ceiling
 * @see Round */
public enum Floor implements ScalarUnaryOperator {
  FUNCTION;

  public static final ScalarUnaryOperator _1 = Floor.toMultipleOf(StaticHelper._1);
  public static final ScalarUnaryOperator _2 = Floor.toMultipleOf(StaticHelper._2);
  public static final ScalarUnaryOperator _3 = Floor.toMultipleOf(StaticHelper._3);
  public static final ScalarUnaryOperator _4 = Floor.toMultipleOf(StaticHelper._4);
  public static final ScalarUnaryOperator _5 = Floor.toMultipleOf(StaticHelper._5);
  public static final ScalarUnaryOperator _6 = Floor.toMultipleOf(StaticHelper._6);
  public static final ScalarUnaryOperator _7 = Floor.toMultipleOf(StaticHelper._7);
  public static final ScalarUnaryOperator _8 = Floor.toMultipleOf(StaticHelper._8);
  public static final ScalarUnaryOperator _9 = Floor.toMultipleOf(StaticHelper._9);

  /** @param scalar instance if {@link RealScalar}
   * @return best integer scalar approximation to floor of scalar
   * @throws Throw if scalar is Infinity, or NaN */
  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RoundingInterface)
      return ((RoundingInterface) scalar).floor();
    if (scalar instanceof MultiplexScalar) {
      MultiplexScalar multiplexScalar = (MultiplexScalar) scalar;
      return multiplexScalar.eachMap(FUNCTION);
    }
    throw new Throw(scalar);
  }

  /** Examples:
   * <pre>
   * Floor[+4.0] == +4
   * Floor[+4.2] == +4
   * Floor[+4.9] == +4
   * Floor[-7.2] == -8
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
   * @return tensor with all entries replaced by their floor */
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
