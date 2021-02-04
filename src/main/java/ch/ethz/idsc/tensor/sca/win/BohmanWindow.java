// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** BohmanWindow[1/2]=0
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BohmanWindow.html">BohmanWindow</a> */
public enum BohmanWindow implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar x) {
    if (StaticHelper.SEMI.isInside(x)) {
      x = Abs.FUNCTION.apply(x);
      Scalar x2pi = Pi.TWO.multiply(x);
      Scalar a = RealScalar.ONE.subtract(x.add(x)).multiply(Cos.FUNCTION.apply(x2pi));
      Scalar b = Sin.FUNCTION.apply(x2pi).divide(Pi.VALUE);
      return a.add(b);
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
