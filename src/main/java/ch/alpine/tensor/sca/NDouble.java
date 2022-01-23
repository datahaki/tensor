// code by jph
package ch.alpine.tensor.sca;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.NInterface;

/* package */ class NDouble extends N {
  static final N INSTANCE = new NDouble();

  private NDouble() {
    // ---
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return scalar instanceof NInterface nInterface //
        ? nInterface.n()
        : Objects.requireNonNull(scalar);
  }
}
