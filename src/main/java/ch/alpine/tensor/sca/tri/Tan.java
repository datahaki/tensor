// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <pre>
 * Tan[NaN] == NaN
 * </pre>
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/Tan.html">Tan</a>
 * 
 * @see ArcTan */
public enum Tan implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RealScalar)
      return DoubleScalar.of(Math.tan(scalar.number().doubleValue()));
    if (scalar instanceof ComplexScalar z)
      return Sin.FUNCTION.apply(z).divide(Cos.FUNCTION.apply(z));
    throw TensorRuntimeException.of(scalar);
  }

  /** @param tensor
   * @return tensor with all entries replaced by their tan */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
