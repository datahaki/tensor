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
    return scalar instanceof NInterface nInterface //
        ? nInterface.n(mathContext)
        : Objects.requireNonNull(scalar);
  }
}
