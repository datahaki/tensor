// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Abs;

/** BartlettHannWindow[1/2]=0
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BartlettHannWindow.html">BartlettHannWindow</a> */
public enum BartlettHannWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = RationalScalar.of(31, 50);
  private static final Scalar A1 = RationalScalar.of(19, 50);
  private static final Scalar L1 = RationalScalar.of(-12, 25);

  @Override
  public Scalar apply(Scalar x) {
    if (StaticHelper.SEMI.isInside(x)) {
      x = Abs.FUNCTION.apply(x);
      return StaticHelper.deg1(A0, A1, x).add(x.pmul(L1));
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
