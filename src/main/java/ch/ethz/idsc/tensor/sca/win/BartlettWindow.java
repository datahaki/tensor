// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Abs;

/** triangular function max(0, 1 - 2*|x|)
 * 
 * BartlettWindow[1/2]=0
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BartlettWindow.html">BartlettWindow</a> */
public enum BartlettWindow implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar x) {
    if (StaticHelper.SEMI.isInside(x)) {
      x = Abs.FUNCTION.apply(x);
      return RealScalar.ONE.subtract(x.add(x));
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their function value */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
