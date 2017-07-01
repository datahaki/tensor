// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Multinomial;

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
  // ---
  private static final Scalar THRESHOLD = RealScalar.of(0.05);
  private static final Tensor SERIES = Tensors.vector(1, 0, -6, 0, 120, 0, -5040, 0, 362880, 0, -39916800) //
      .map(InvertUnlessZero.FUNCTION);

  @Override
  public Scalar apply(Scalar scalar) {
    if (Scalars.lessThan(scalar.abs(), THRESHOLD))
      return Multinomial.horner(SERIES, N.FUNCTION.apply(scalar));
    return Sin.FUNCTION.apply(scalar).divide(scalar);
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their sin */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
