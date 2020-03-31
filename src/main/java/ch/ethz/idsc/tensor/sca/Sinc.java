// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** for a complex scalar z the Sinc function is defined as
 * <pre>
 * Sinc[z] = Sin[z] / z
 * Sinc[0] = 1
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sinc.html">Sinc</a> */
public enum Sinc implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
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
