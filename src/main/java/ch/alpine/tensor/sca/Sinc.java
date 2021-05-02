// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** for a complex scalar z the Sinc function is defined as
 * <pre>
 * Sinc[z] = Sin[z] / z
 * Sinc[0] = 1
 * </pre>
 * 
 * <pre>
 * Sinc[+Infinity] = 0
 * Sinc[-Infinity] = 0
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sinc.html">Sinc</a> */
public enum Sinc implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar.equals(DoubleScalar.POSITIVE_INFINITY) || //
        scalar.equals(DoubleScalar.NEGATIVE_INFINITY))
      return RealScalar.ZERO;
    Scalar sin = Sin.FUNCTION.apply(scalar);
    return Scalars.isZero(scalar) //
        ? RealScalar.ONE
        : sin.divide(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their sinc */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
