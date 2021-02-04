// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/* package */ abstract class ParameterizedWindow implements ScalarUnaryOperator {
  private static final long serialVersionUID = 6777460640385425055L;
  // ---
  protected final Scalar alpha;

  protected ParameterizedWindow(Scalar alpha) {
    this.alpha = alpha;
  }

  @Override
  public final Scalar apply(Scalar x) {
    return StaticHelper.SEMI.isInside(x) //
        ? evaluate(x)
        : RealScalar.ZERO;
  }

  /** @param x guaranteed in the interval [-1/2, 1/2]
   * @return */
  protected abstract Scalar evaluate(Scalar x);

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), alpha);
  }
}
