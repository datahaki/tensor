// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.qty.Quantity;

/** Abs is consistent with Mathematica for {@link RealScalar}, {@link ComplexScalar},
 * {@link Quaternion}, and {@link Quantity}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Abs.html">Abs</a> */
public enum Abs implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof AbsInterface) {
      AbsInterface absInterface = (AbsInterface) scalar;
      return absInterface.abs();
    }
    throw TensorRuntimeException.of(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their absolute value */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }

  /** @param a
   * @param b
   * @return |a - b| */
  public static Scalar between(Scalar a, Scalar b) {
    return FUNCTION.apply(a.subtract(b));
  }
}
