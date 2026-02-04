// code by jph
package ch.alpine.tensor.sca;

import java.util.Objects;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.lie.rot.Quaternion;
import ch.alpine.tensor.qty.Quantity;

/** Sign is consistent with Mathematica for {@link RealScalar}, {@link ComplexScalar},
 * {@link Quaternion}, and {@link Quantity}.
 *
 * <p>Sign gives the signum of a scalar provided by the implementation of {@link SignInterface}.
 *
 * <p>If the scalar type does not implement {@link SignInterface}, then an exception is thrown.
 * 
 * <p>Sign offers predicates to check positive, non-negative, negative, and non-positive scalars.
 * As checks for zero, and non-zero use {@link Scalars#isZero(Scalar)}, and {@link Scalars#nonZero(Scalar)}.
 * 
 * <pre>
 * Sign[NaN] == NaN
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sign.html">Sign</a> */
public enum Sign implements ScalarUnaryOperator {
  FUNCTION;

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof SignInterface signInterface)
      return signInterface.sign();
    throw new Throw(scalar);
  }

  // ---
  /** function is equivalent to
   * <code>Scalars.lessThan(scalar.zero(), scalar)</code>
   * 
   * @param scalar may be instance of {@link Quantity}
   * @return true if sign of given scalar evaluates to +1 */
  public static boolean isPositive(Scalar scalar) {
    return Scalars.lessThan(scalar.zero(), scalar);
  }

  /** function is equivalent to
   * <code>Scalars.lessEquals(scalar.zero(), scalar)</code>
   * 
   * @param scalar may be instance of {@link Quantity}
   * @return true if sign of given scalar evaluates to +1, or 0 */
  public static boolean isPositiveOrZero(Scalar scalar) {
    return Scalars.lessEquals(scalar.zero(), scalar);
  }

  /** function is equivalent to
   * <code>Scalars.lessThan(scalar, scalar.zero())</code>
   * 
   * @param scalar may be instance of {@link Quantity}
   * @return true if sign of given scalar evaluates to -1 */
  public static boolean isNegative(Scalar scalar) {
    return Scalars.lessThan(scalar, scalar.zero());
  }

  /** function is equivalent to
   * <code>Scalars.lessEquals(scalar, scalar.zero())</code>
   * 
   * @param scalar may be instance of {@link Quantity}
   * @return true if sign of given scalar evaluates to -1, or 0 */
  public static boolean isNegativeOrZero(Scalar scalar) {
    return Scalars.lessEquals(scalar, scalar.zero());
  }

  // ---
  /** Remark: Functionality inspired by {@link Objects#requireNonNull(Object)}
   * 
   * @param scalar
   * @return scalar
   * @throws Exception if given scalar is not positive, i.e. has negative or zero sign */
  public static Scalar requirePositive(Scalar scalar) {
    if (isPositive(scalar))
      return scalar;
    throw new Throw(scalar);
  }

  /** Remark: Functionality inspired by {@link Objects#requireNonNull(Object)}
   * 
   * @param scalar
   * @return scalar
   * @throws Exception if given scalar is negative, i.e. has negative sign */
  public static Scalar requirePositiveOrZero(Scalar scalar) {
    if (isPositiveOrZero(scalar))
      return scalar;
    throw new Throw(scalar);
  }
}
