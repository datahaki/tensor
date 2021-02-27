// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Sinc;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LanczosWindow.html">LanczosWindow</a> */
public enum LanczosWindow implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? Sinc.FUNCTION.apply(x.multiply(Pi.TWO))
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
