// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** HammingWindow[1/2]=0.08695652173913038
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HammingWindow.html">HammingWindow</a> */
public enum HammingWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = Rational.of(25, 46);
  private static final Scalar A1 = Rational.of(21, 46);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg1(A0, A1, x)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return "HammingWindow";
  }
}
