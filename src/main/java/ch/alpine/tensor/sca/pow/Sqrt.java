// code by jph
package ch.alpine.tensor.sca.pow;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <pre>
 * Sqrt[NaN] == NaN
 * </pre>
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sqrt.html">Sqrt</a> */
public enum Sqrt implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof SqrtInterface sqrtInterface)
      return sqrtInterface.sqrt();
    throw new Throw(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their square root */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
