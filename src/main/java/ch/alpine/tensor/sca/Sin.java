// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TrigonometryInterface;

/** Reference:
 * <a href="http://www.milefoot.com/math/complex/functionsofi.htm">functions of i</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sin.html">Sin</a>
 * 
 * @see ArcSin */
public enum Sin implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof TrigonometryInterface) {
      TrigonometryInterface trigonometryInterface = (TrigonometryInterface) scalar;
      return trigonometryInterface.sin();
    }
    throw TensorRuntimeException.of(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their sin */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
