// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;

/* package */ abstract class ParameterizedWindow implements ScalarUnaryOperator {
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

  @Override // from Object
  public final String toString() {
    return MathematicaFormat.concise(title(), alpha);
  }

  /** @param x guaranteed to be in the interval [-1/2, 1/2]
   * @return */
  protected abstract Scalar evaluate(Scalar x);

  /** @return getClass().getSimpleName() */
  protected abstract String title();
}
