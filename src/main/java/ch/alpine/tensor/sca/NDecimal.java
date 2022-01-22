// code by jph
package ch.alpine.tensor.sca;

import java.math.MathContext;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.NInterface;

/* package */ class NDecimal extends N {
  private final MathContext mathContext;

  public NDecimal(MathContext mathContext) {
    this.mathContext = mathContext;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof NInterface nInterface)
      return nInterface.n(mathContext);
    return Objects.requireNonNull(scalar);
  }
}
