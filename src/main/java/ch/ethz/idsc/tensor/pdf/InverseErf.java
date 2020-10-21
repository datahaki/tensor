// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** <pre>
 * InverseErf[p] == InverseErfc[1 - p]
 * </pre>
 * 
 * <p>Reference:
 * "Incomplete Gamma Function" in NR, 2007
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/InverseErf.html">InverseErf</a> */
public enum InverseErf implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return InverseErfc.FUNCTION.apply(RealScalar.ONE.subtract(scalar));
  }

  /** @param tensor
   * @return tensor with all scalar entries replaced by the evaluation under InverseErf */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
