// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ArgInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.qty.Quantity;

/** Arg is consistent with Mathematica for {@link RealScalar}, {@link ComplexScalar},
 * {@link Quaternion}, and {@link Quantity}.
 *
 * <p>Arg gives the argument of a number in the complex plane.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Arg.html">Arg</a> */
public enum Arg implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ArgInterface argInterface)
      return argInterface.arg();
    throw TensorRuntimeException.of(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their argument */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
