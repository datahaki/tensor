// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** BlackmanWindow[1/2]=-1.3877787807814457E-17
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanWindow.html">BlackmanWindow</a> */
public enum BlackmanWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = RationalScalar.of(21, 50);
  private static final Scalar A2 = RationalScalar.of(2, 25);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg2(A0, RationalScalar.HALF, A2, x)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return "BlackmanWindow";
  }
}
