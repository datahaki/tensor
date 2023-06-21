// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.Quaternion;

/** the purpose of AbsSquared is to preserve the precision when working with complex numbers.
 * Since {@link ComplexScalar}::abs involves a square root the square of the absolute value
 * is better computed using <code>z * conjugate(z)</code>. Analogous for {@link Quaternion}s.
 * 
 * <p>If a {@link Scalar} does not implement {@link AbsInterface}, then the function
 * AbsSquared throws an Exception. */
public enum AbsSquared implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof AbsInterface absInterface)
      return absInterface.absSquared();
    throw new Throw(scalar);
  }

  /** @param a
   * @param b
   * @return |a - b| ^ 2 */
  public static Scalar between(Scalar a, Scalar b) {
    return FUNCTION.apply(a.subtract(b));
  }
}
