// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.tri.Sinc;

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
    return "LanczosWindow";
  }
}
