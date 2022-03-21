// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** gives the exponential of a {@link Scalar} that implements {@link ExpInterface}.
 * Supported types include {@link RealScalar}, and {@link ComplexScalar}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Exp.html">Exp</a>
 * 
 * @see ExpInterface
 * @see Log */
public enum Exp implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ExpInterface)
      return ((ExpInterface) scalar).exp();
    throw TensorRuntimeException.of(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their exponential */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
