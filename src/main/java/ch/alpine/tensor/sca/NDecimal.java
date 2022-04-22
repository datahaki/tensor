// code by jph
package ch.alpine.tensor.sca;

import java.math.MathContext;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.NInterface;

/* package */ class NDecimal extends N {
  private final MathContext mathContext;

  public NDecimal(MathContext mathContext) {
    this.mathContext = mathContext;
  }

  @Override
  protected Scalar numeric(NInterface nInterface) {
    return nInterface.n(mathContext);
  }
}
