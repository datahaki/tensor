// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** BlackmanHarrisWindow[1/2]=6.0000000000001025E-5
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanHarrisWindow.html">BlackmanHarrisWindow</a> */
public enum BlackmanHarrisWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = Rational.of(35875, 100000);
  private static final Scalar A1 = Rational.of(48829, 100000);
  private static final Scalar A2 = Rational.of(14128, 100000);
  private static final Scalar A3 = Rational.of(1168, 100000);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg3(A0, A1, A2, A3, x)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return "BlackmanHarrisWindow";
  }
}
