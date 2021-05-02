// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Gudermannian.html">Gudermannian</a> */
public enum Gudermannian implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    Scalar value = ArcTan.FUNCTION.apply(Exp.FUNCTION.apply(scalar));
    return value.add(value).subtract(Pi.HALF);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their gudermannian evaluation */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
