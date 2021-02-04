// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** ExactBlackmanWindow[1/2]=0.006878761822871883
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ExactBlackmanWindow.html">ExactBlackmanWindow</a> */
public enum ExactBlackmanWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar A0 = RationalScalar.of(3969, 9304);
  private static final Scalar A1 = RationalScalar.of(4620, 9304);
  private static final Scalar A2 = RationalScalar.of(715, 9304);

  @Override
  public Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? StaticHelper.deg2(A0, A1, A2, x)
        : RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
