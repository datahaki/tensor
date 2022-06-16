// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** NuttallWindow[1/2]=-2.42861286636753E-17
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/NuttallWindow.html">NuttallWindow</a> */
public enum NuttallWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = RationalScalar.of(88942, 250000);
  private static final Scalar A1 = RationalScalar.of(121849, 250000);
  private static final Scalar A2 = RationalScalar.of(36058, 250000);
  private static final Scalar A3 = RationalScalar.of(3151, 250000);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg3(A0, A1, A2, A3, x)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return "NuttallWindow";
  }
}
