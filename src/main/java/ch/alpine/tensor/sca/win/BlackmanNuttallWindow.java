// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** BlackmanNuttallWindow[1/2]=3.628000000000381E-4
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BlackmanNuttallWindow.html">BlackmanNuttallWindow</a> */
public enum BlackmanNuttallWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = RationalScalar.of(3635819, 10000000);
  private static final Scalar A1 = RationalScalar.of(4891775, 10000000);
  private static final Scalar A2 = RationalScalar.of(1365995, 10000000);
  private static final Scalar A3 = RationalScalar.of(106411, 10000000);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg3(A0, A1, A2, A3, x)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return "BlackmanNuttallWindow";
  }
}
