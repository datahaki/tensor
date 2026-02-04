// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.rot.Quaternion;
import ch.alpine.tensor.qty.Quantity;

/** Abs is consistent with Mathematica for {@link RealScalar}, {@link ComplexScalar},
 * {@link Quaternion}, and {@link Quantity}.
 * 
 * <pre>
 * Abs[NaN] == NaN
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Abs.html">Abs</a> */
public enum Abs implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof AbsInterface absInterface)
      return absInterface.abs();
    throw new Throw(scalar);
  }

  /** @param a
   * @param b
   * @return |a - b| */
  public static Scalar between(Scalar a, Scalar b) {
    return FUNCTION.apply(a.subtract(b));
  }
}
