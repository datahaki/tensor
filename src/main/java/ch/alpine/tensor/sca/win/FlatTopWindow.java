// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** the flat-top window function also evaluates to negative values
 * 
 * FlatTopWindow[1/2]=-4.210539999999997E-4
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FlatTopWindow.html">FlatTopWindow</a> */
public enum FlatTopWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = Rational.of(215578947, 1000000000);
  private static final Scalar A1 = Rational.of(416631580, 1000000000);
  private static final Scalar A2 = Rational.of(277263158, 1000000000);
  private static final Scalar A3 = Rational.of(83578947, 1000000000);
  private static final Scalar A4 = Rational.of(6947368, 1000000000);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg4(A0, A1, A2, A3, A4, x)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return "FlatTopWindow";
  }
}
