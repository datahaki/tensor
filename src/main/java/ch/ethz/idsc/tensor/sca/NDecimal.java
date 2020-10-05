// code by jph
package ch.ethz.idsc.tensor.sca;

import java.math.MathContext;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;

/* package */ class NDecimal extends N {
  private static final long serialVersionUID = -9144500095628716632L;
  private final MathContext mathContext;

  public NDecimal(MathContext mathContext) {
    this.mathContext = mathContext;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof NInterface) {
      NInterface nInterface = (NInterface) scalar;
      return nInterface.n(mathContext);
    }
    return Objects.requireNonNull(scalar);
  }
}
