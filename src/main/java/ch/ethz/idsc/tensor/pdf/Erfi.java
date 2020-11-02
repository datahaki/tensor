// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;

/** <pre>
 * Erfi[z] == -I * Erf[I z]
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Erfi.html">Erfi</a> */
public enum Erfi implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar I_NEGATE = ComplexScalar.of(0.0, -1.0);

  @Override
  public Scalar apply(Scalar z) {
    Scalar re = Real.FUNCTION.apply(z);
    Scalar im = Imag.FUNCTION.apply(z);
    return I_NEGATE.multiply(Erf.FUNCTION.apply(ComplexScalar.of(im.negate(), re)));
  }

  /** @param tensor
   * @return tensor with all scalar entries replaced by the evaluation under Erfi */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
